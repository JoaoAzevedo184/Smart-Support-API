# Design Patterns

Este é o documento central do projeto. Cada padrão abaixo traz: **o problema** que ele resolve neste domínio, **onde** ele é aplicado e um esboço de **como** fica o código. A ideia é que um avaliador consiga ligar teoria e prática rapidamente.

---

## 1. Factory Method — `factory`

**Problema.** Existem vários tipos de chamado (bug, cobrança, solicitação de feature), e cada um nasce com defaults diferentes (categoria, prioridade inicial, processador). Espalhar `new Ticket(...)` com esses defaults pelo código duplica regra.

**Solução.** Uma fábrica concentra a criação:

```java
Ticket bug     = ticketFactory.create(TicketType.BUG, dto);
Ticket billing = ticketFactory.create(TicketType.BILLING, dto);
Ticket feature = ticketFactory.create(TicketType.FEATURE_REQUEST, dto);
```

Cada tipo conhece seus próprios defaults; o chamador só informa o tipo.

---

## 2. Builder — `builder`

**Problema.** `Ticket` tem muitos atributos (título, descrição, status, prioridade, categoria, cliente, equipe, timestamps). Um construtor com 8+ parâmetros é ilegível e propenso a erro de ordem.

**Solução.** Construção fluente:

```java
Ticket ticket = Ticket.builder()
        .title("Erro ao gerar boleto")
        .description("...")
        .category(Category.BILLING)
        .priority(Priority.HIGH)
        .status(Status.ABERTO)
        .client(client)
        .build();
```

> Pode ser o `@Builder` do Lombok ou um builder manual. Para o portfólio, vale documentar a escolha; um builder manual demonstra entendimento, o do Lombok demonstra pragmatismo.

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

## 5. Observer — `observer`

**Problema.** Quando o status do chamado muda, várias coisas precisam reagir: enviar e-mail, postar no Slack, registrar auditoria, atualizar dashboard. Chamar tudo isso manualmente acopla o serviço de status a cada destino.

**Solução.** O `Ticket` (subject) notifica observadores registrados:

```text
Status muda ──► notifica ──► EmailNotification
                          ├─► SlackNotification
                          ├─► AuditLog
                          └─► DashboardUpdater
```

> No Spring, a forma idiomática é usar **Spring Events** (`ApplicationEventPublisher` + `@EventListener`). Vale implementar o Observer "clássico" para demonstrar o padrão e mencionar que Spring Events é a versão de produção.

---

## 6. Singleton — beans Spring

**Problema.** Serviços como logger de auditoria, serviço de e-mail e gerenciador de notificações devem ter uma única instância compartilhada.

**Solução.** No Spring, o escopo padrão de um bean **já é singleton**. Marcar `AuditLogger`, `EmailService` e `NotificationManager` como `@Service`/`@Component` resolve sem implementação manual. Documentar isso mostra que você entende como o contêiner aplica o padrão por baixo.

---

## 7. Adapter — `adapter`

**Problema.** A empresa tem um sistema legado de notificação (`LegacyNotification`) cuja interface não bate com a `NotificationService` usada pela aplicação.

**Solução.** Um adaptador traduz uma interface na outra:

```java
public class LegacyNotificationAdapter implements NotificationService {
    private final LegacyNotification legacy;
    @Override
    public void send(Notification n) {
        legacy.dispatch(n.recipient(), n.title() + ": " + n.body());
    }
}
```

A aplicação programa contra `NotificationService` e ignora que, por trás, há um sistema antigo.

---

## 8. Facade — `facade`

**Problema.** Criar um chamado dispara muitos serviços (validar, calcular prioridade, notificar, persistir, auditar). O controller não deveria conhecer essa orquestração.

**Solução.** Uma fachada expõe um único ponto de entrada:

```java
public TicketResponse createTicket(CreateTicketRequest req) {
    Ticket ticket = factory.create(req.type(), req);
    pipeline.process(ticket);     // Chain
    Ticket saved = repository.save(ticket);
    events.publishCreated(saved); // Observer
    return mapper.toResponse(saved);
}
```

O controller chama `facade.createTicket(req)` e nada mais.

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

## Resumo

| Padrão | Categoria GoF | Papel no projeto |
| --- | --- | --- |
| Factory Method | Criacional | Criar tipos de chamado |
| Builder | Criacional | Montar `Ticket` |
| Singleton | Criacional | Serviços únicos (via Spring) |
| Adapter | Estrutural | Integrar legado |
| Facade | Estrutural | Orquestrar criação |
| Strategy | Comportamental | Calcular prioridade |
| Chain of Responsibility | Comportamental | Pipeline do chamado |
| Observer | Comportamental | Reagir a mudança de status |
| Template Method | Comportamental | Esqueleto de processamento |
| Command | Comportamental | Ações do chamado |