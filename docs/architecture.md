# Arquitetura

## Visão geral

A Smart Support API segue uma arquitetura em camadas (layered architecture) tradicional do ecossistema Spring, com uma separação adicional: os **padrões de projeto** ficam em pacotes próprios, e não diluídos dentro de `service`. Isso é intencional — o projeto é, antes de tudo, uma vitrine de Design Patterns, então a organização do código precisa tornar cada padrão fácil de localizar.

```
HTTP  ─►  Controller  ─►  Facade  ─►  Repository  ─►  PostgreSQL
                              │
                              ├─► Factory      (cria o Ticket conforme a categoria)
                              ├─► Chain         (valida → classifica → prioriza → atribui)
                              │     ├─► Strategy    (regra de prioridade)
                              │     └─► Classifier  (categoria por regras ou IA)
                              ├─► Observer      (notifica ao mudar de status, via Spring Events)
                              │     └─► Adapter  (sistema legado / webhook configurável)
                              └─► Command       (ações: fechar, reabrir, atribuir)
```

## Camadas

### Controller
Responsável apenas por receber a requisição HTTP, validar o DTO de entrada (`@Valid`) e delegar. Não contém regra de negócio. Retorna DTOs de resposta, nunca entidades JPA.

### Facade
A criação de um chamado envolve vários passos (validar, classificar, priorizar, atribuir, persistir, notificar, auditar). Em vez de o controller orquestrar tudo isso, ele chama um único método do `TicketFacade`. A Facade reduz o acoplamento entre o controller e o subsistema de criação.

### Service
Onde mora a regra de negócio "pura" dos agregados de cadastro (Client, User, SupportTeam). O ciclo de vida do chamado é orquestrado pelo `TicketFacade` diretamente sobre os repositórios e os padrões do pipeline. Todos são beans Spring — portanto, **singletons** gerenciados pelo contêiner, o que satisfaz naturalmente esse padrão sem implementação manual.

### Repository
Interfaces Spring Data JPA. Sem implementação manual; o Spring gera as queries.

### Persistência
PostgreSQL, com o schema versionado por **Flyway** (migrations em `src/main/resources/db/migration`). Toda alteração de schema vira uma nova migration versionada, nunca uma edição manual no banco.

## Fluxo do chamado

O ciclo de vida de um chamado é o coração do sistema:

```
Novo chamado
    │
    ▼
Validação          ◄── ValidationHandler (Chain)
    │
    ▼
Classificação      ◄── CategoryHandler (Chain)
    │
    ▼
Prioridade         ◄── PriorityHandler (Chain) + PriorityStrategy
    │
    ▼
Atribuição         ◄── AssignTeamHandler (Chain)
    │
    ▼
Persistência       ◄── Repository
    │
    ▼
Notificação        ◄── Observers (Email, Slack, Audit, Dashboard)
```

A **Chain of Responsibility** modela esse pipeline: cada etapa é um handler que processa o chamado e decide passar adiante (`next.handle(context)`) ou interromper (ex.: validação falha). Adicionar uma nova etapa significa inserir um handler na cadeia, sem tocar nos demais.

A classificação e a priorização respeitam o que o cliente informou: o `CategoryHandler` só classifica quando a `category` não veio no request, e o `PriorityHandler` só resolve a prioridade quando ela não foi enviada explicitamente.

## Ciclo de vida de status

```
OPEN ──────► IN_PROGRESS ──────► RESOLVED ──────► CLOSED ◄──┐
  │              ▲                   │               ▲       │
  │              │                   └──► REOPENED ──┘       │
  └──────────────┴─────── (qualquer estado pode ir a CLOSED) ┘
```

Transições permitidas: `OPEN → {IN_PROGRESS, CLOSED}`, `IN_PROGRESS → {RESOLVED, CLOSED}`, `RESOLVED → {CLOSED, REOPENED}`, `CLOSED → {REOPENED}`, `REOPENED → {IN_PROGRESS, CLOSED}`. São validadas pelo próprio enum `TicketStatus` (`canTransitionTo`); uma transição inválida resulta em **HTTP 409**. Cada transição dispara os **Observers** registrados (via Spring Events). Quando o destino de notificação não fala a interface esperada, um **Adapter** faz a ponte — hoje há um adapter para sistema legado e um `WebhookNotificationAdapter` que envia a notificação por HTTP para uma URL configurável.

## Extensão de IA para classificação

A classificação de categoria entra no pipeline por trás da interface `TicketClassifier`, injetada no `CategoryHandler`. Há duas implementações:

- `RuleBasedClassifier` — baseline por palavra-chave, sem dependência externa (padrão).
- `AiTicketClassifier` — usa um LLM via Spring AI (`ChatClient`), com **Ollama** como provedor padrão e **Gemini** disponível pelo profile `gemini`; qualquer falha da IA cai automaticamente para as regras.

Qual implementação está ativa é decidido por uma **Strategy** (`TicketClassifierResolver`, `app.classifier.strategy`), sem que nenhum outro componente do pipeline mude — a validação prática de que o sistema ficou aberto para extensão (OCP). Detalhes em [`design-patterns.md`](design-patterns.md) e [`roadmap.md`](roadmap.md).

## Princípios seguidos

- **Controllers magros, serviços coesos.** Nenhuma regra de negócio em controller.
- **DTOs na fronteira.** Entidades JPA não cruzam a borda HTTP.
- **Schema versionado.** Toda mudança de banco é uma migration Flyway.
- **Padrões com propósito.** Cada padrão resolve um problema concreto do domínio; nenhum é forçado.