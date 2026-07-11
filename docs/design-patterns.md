# Design Patterns

Este é o documento central do projeto. Cada padrão abaixo traz: **o problema** que ele resolve neste domínio, **onde** ele é aplicado e um esboço de **como** fica o código. A ideia é que um avaliador consiga ligar teoria e prática rapidamente.

---

## 1. Factory Method — `factory`

**Problema.** Existem vários tipos de chamado (bug, cobrança, suporte), e cada um nasce com defaults diferentes (prioridade inicial, processador). Espalhar `new Ticket(...)` com esses defaults pelo código duplica regra.

**Solução.** Uma fábrica por categoria concentra a criação. `TicketFactoryProvider` resolve a fábrica certa a partir da `TicketCategory`:

```java
TicketFactory factory = ticketFactoryProvider.getFactory(TicketCategory.BUG);
Ticket ticket = factory.createTicket(title, description, client, requestedPriority);
```

Implementações: `BugTicketFactory` (default `HIGH`), `BillingTicketFactory`, `SupportTicketFactory` (default `LOW`). Cada tipo conhece seus próprios defaults; o chamador só informa a categoria.

---

## 2. Builder — `builder`

**Problema.** `Ticket` tem muitos atributos (título, descrição, status, prioridade, categoria, cliente, equipe, timestamps). Um construtor com 8+ parâmetros é ilegível e propenso a erro de ordem.

**Solução.** Construção fluente:

```java
Ticket ticket = Ticket.builder()
        .title("Erro ao gerar boleto")
        .description("...")
        .category(TicketCategory.BILLING)
        .priority(TicketPriority.HIGH)
        .status(TicketStatus.OPEN)
        .client(client)
        .build();
```

> O projeto usa o `@Builder` do Lombok na entidade `Ticket`, encapsulado por `TicketBuilder`/as fábricas — pragmatismo sem abrir mão de leitura fluente na construção.

---

## 3. Strategy — `strategy`

**Problema.** A prioridade de um chamado é calculada de formas diferentes dependendo de regras de negócio (cliente VIP, tipo, SLA). Um `if/else` gigante fica difícil de manter e testar.

**Solução.** Uma interface e implementações intercambiáveis:

```java
public interface PriorityStrategy {
    Priority calculate(Ticket ticket);
}
```

Implementações: `UrgentPriorityStrategy`, `HighPriorityStrategy`, `MediumPriorityStrategy`, `LowPriorityStrategy`. A estratégia certa é selecionada em runtime e injetada no handler de prioridade.

**Segundo uso — canal de notificação.** O mesmo padrão resolve qual canal de notificação está ativo (webhook, log ou no-op). `NotificationChannelResolver` recebe todas as implementações de `NotificationChannelSender` via injeção de lista (Spring) e seleciona a ativa conforme `app.notifications.channel`, com fallback para `log` quando o valor é desconhecido.

**Terceiro uso — classificador de chamado.** `TicketClassifierResolver` (pacote `classifier`) aplica a mesma ideia para escolher entre `RuleBasedClassifier` (baseline por palavra-chave) e `AiTicketClassifier` (via LLM), conforme `app.classifier.strategy`, com fallback para regras quando o valor é desconhecido — ver seção 11.

---

## 4. Chain of Responsibility — `chain`

**Problema.** Um chamado precisa passar por etapas sequenciais (validar, detectar spam, classificar, priorizar, atribuir). Acoplar tudo num método só viola o princípio de responsabilidade única e dificulta inserir/remover etapas.

**Solução.** Cada etapa é um handler:

```text
ValidationHandler → SpamHandler → CategoryHandler → PriorityHandler → AssignTeamHandler → Finish
```

```java
public abstract class TicketHandler {
    protected TicketHandler next;
    public TicketHandler setNext(TicketHandler next) { this.next = next; return next; }
    public abstract void handle(Ticket ticket);
    protected void forward(Ticket ticket) {
        if (next != null) next.handle(ticket);
    }
}
```

Inserir uma etapa nova (ex.: deduplicação) é adicionar um elo — sem tocar nos existentes. **Este é o padrão de maior destaque do projeto.**

---

## 5. Observer — `event`

**Problema.** Quando o status do chamado muda (ou ele é atribuído), várias coisas precisam reagir: enviar e-mail, postar no Slack, registrar auditoria, atualizar dashboard. Chamar tudo isso manualmente acopla a Facade a cada destino.

**Solução.** A `TicketFacade` publica um evento de domínio; os observadores reagem de forma independente:

```text
changeStatus ──► TicketStatusChangedEvent ──► EmailNotificationListener
assign       ──► TicketAssignedEvent      ├─► SlackNotificationListener
                                          ├─► AuditLogListener
                                          └─► DashboardNotificationListener ──► NotificationSender (Adapter)
```

> Implementado com **Spring Events** (`ApplicationEventPublisher` + `@EventListener`), a forma idiomática do framework — por isso o pacote se chama `event`. Os eventos são `TicketStatusChangedEvent` e `TicketAssignedEvent`; cada listener é um `@Component` desacoplado, e adicionar um novo canal é criar mais um listener, sem tocar na Facade.

---

## 6. Singleton — beans Spring

**Problema.** Serviços como os resolvers (`PriorityResolver`, `NotificationChannelResolver`, `TicketClassifierResolver`), a Facade e os listeners devem ter uma única instância compartilhada.

**Solução.** No Spring, o escopo padrão de um bean **já é singleton**. Marcar essas classes como `@Service`/`@Component` resolve sem implementação manual. Documentar isso mostra que você entende como o contêiner aplica o padrão por baixo.

---

## 7. Adapter — `adapter`

**Problema.** A empresa tem um sistema legado de notificação (`LegacyNotificationSystem`) cuja assinatura de método não bate com a interface moderna `NotificationSender` usada pela aplicação.

**Solução.** Um adaptador traduz uma interface na outra:

```java
public class LegacyNotificationAdapter implements NotificationSender {
    private final LegacyNotificationSystem legacyNotificationSystem;
    @Override
    public void notify(String recipient, String message) {
        legacyNotificationSystem.dispatch(recipient, message, DEFAULT_SEVERITY_CODE);
    }
}
```

A aplicação programa contra `NotificationSender` e ignora que, por trás, há um sistema antigo.

**Segunda implementação — `WebhookNotificationAdapter`.** A mesma interface `NotificationSender` ganhou uma nova implementação que traduz a notificação em um `POST` HTTP com payload JSON (`evento`, `chamado`, `timestamp`) para uma URL configurável (`app.notifications.webhook-url`). Falhas de rede são absorvidas com retry simples, sem interromper o fluxo principal; na ausência de URL configurada, a notificação apenas é registrada em log.

Como agora existem várias implementações de `NotificationSender` (legado, webhook, log, no-op), a escolha de qual está ativa em runtime é feita por uma **Strategy** (`NotificationChannelResolver`, ver seção 3), com base em `app.notifications.channel`. Isso evita `if/else` espalhado e mantém os consumidores (como os listeners de evento) desacoplados do canal escolhido.

---

## 8. Facade — `facade`

**Problema.** Criar um chamado dispara muitos serviços (validar, calcular prioridade, notificar, persistir, auditar). O controller não deveria conhecer essa orquestração.

**Solução.** Uma fachada expõe um único ponto de entrada:

```java
public TicketResponse openTicket(TicketRequest request) {
    Client client = clientRepository.findById(request.clientId())...;
    TicketFactory factory = ticketFactoryProvider.getFactory(initialCategory);
    Ticket ticket = factory.createTicket(...);          // Factory + Builder
    ticketProcessingChain.process(                       // Chain (→ Strategy, Classifier)
            new TicketProcessingContext(ticket, request));
    return ticketMapper.toResponse(ticketRepository.save(ticket));
}
```

O controller chama `ticketFacade.openTicket(request)` e nada mais. Mudanças de status/atribuição (que publicam os eventos de **Observer**) passam por métodos irmãos da mesma Facade (`changeStatus`, `assign`).

---

## 9. Template Method — `template`

**Problema.** Bug, cobrança e suporte compartilham um esqueleto de processamento (abrir → validar → processar → finalizar), mas o passo "processar" difere por categoria.

**Solução.** Uma classe abstrata fixa o esqueleto e deixa os passos variáveis abstratos:

```java
public abstract class AbstractTicketProcessor {
    public final void process(Ticket t) {  // template method
        open(t);
        validate(t);
        doProcess(t);   // varia
        finish(t);
    }
    protected abstract void doProcess(Ticket t);
}
```

Implementações: `BugProcessor`, `BillingProcessor`, `SupportProcessor`.

---

## 10. Command — `command`

**Problema.** Ações sobre o chamado (fechar, reabrir, atribuir) precisam ser tratadas de forma uniforme, possivelmente registradas em log ou enfileiradas.

**Solução.** Cada ação vira um objeto com `execute()`:

```java
public interface TicketCommand {
    void execute();
}
```

`CloseTicketCommand`, `AssignTicketCommand`, `ReopenTicketCommand`. Isso desacopla "quem pede a ação" de "quem a executa" e abre caminho para histórico/undo.

---

## 11. Extensão de IA — `classifier`

**Problema.** Quando o cliente não informa a categoria do chamado, alguém precisa inferi-la a partir do texto. Uma implementação por regras (palavras-chave) é determinística e não tem custo, mas é limitada; um LLM generaliza melhor, mas é externo, mais lento e pode falhar. O pipeline (`CategoryHandler`) não deveria conhecer qual dos dois está por trás.

**Solução.** Uma interface `TicketClassifier` já existente no pipeline ganha uma segunda implementação, sem alterar o `CategoryHandler`:

```java
public interface TicketClassifier {
    TicketCategory classify(String title, String description);
}
```

- `RuleBasedClassifier` — baseline por palavra-chave, sempre disponível.
- `AiTicketClassifier` — programa contra `ChatClient` (Spring AI), não contra um provedor específico; se a IA falhar ou responder algo não reconhecido, delega para `RuleBasedClassifier`.

O `CategoryHandler` só chama o classificador quando a categoria não foi informada explicitamente (mesma semântica do `hasExplicitPriority()` da Fase 4). Qual classificador está ativo — e, dentro do `AiTicketClassifier`, qual motor de IA responde (Ollama por padrão, Gemini via profile `application-gemini.yaml`) — é decidido pela **Strategy** `TicketClassifierResolver`/`spring.ai.model.chat`, não pelo `CategoryHandler`. Essa é a validação prática de OCP: a Fase 6 inteira coube sem tocar em `TicketProcessingChain`, `AssignTeamHandler` ou `PriorityHandler`.

---

## Resumo

| Padrão | Categoria GoF | Papel no projeto |
| --- | --- | --- |
| Factory Method | Criacional | Criar chamados por categoria |
| Builder | Criacional | Montar `Ticket` |
| Singleton | Criacional | Serviços únicos (via Spring) |
| Adapter | Estrutural | Integrar sistema legado e webhook de notificação |
| Facade | Estrutural | Orquestrar criação |
| Strategy | Comportamental | Prioridade, canal de notificação e modo de classificação |
| Chain of Responsibility | Comportamental | Pipeline do chamado |
| Observer | Comportamental | Reagir a mudança de status/atribuição (Spring Events) |
| Template Method | Comportamental | Esqueleto de processamento por categoria |
| Command | Comportamental | Ações do chamado (fechar, reabrir, atribuir) |

> A classificação por IA (seção 11) é uma **extensão** aplicada sobre Strategy + Adapter já existentes, não um 11º padrão GoF.