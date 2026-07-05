# Arquitetura

## Visão geral

A Smart Support API segue uma arquitetura em camadas (layered architecture) tradicional do ecossistema Spring, com uma separação adicional: os **padrões de projeto** ficam em pacotes próprios, e não diluídos dentro de `service`. Isso é intencional — o projeto é, antes de tudo, uma vitrine de Design Patterns, então a organização do código precisa tornar cada padrão fácil de localizar.

```
HTTP  ─►  Controller  ─►  Facade  ─►  Service  ─►  Repository  ─►  PostgreSQL
                              │
                              ├─► Factory      (cria o Ticket conforme o tipo)
                              ├─► Chain         (valida → classifica → prioriza → atribui)
                              │     └─► Strategy (regra de prioridade)
                              ├─► Observer      (notifica ao mudar de status)
                              │     └─► Adapter  (sistema legado de notificação)
                              └─► Command       (ações: fechar, reabrir, atribuir)
```

## Camadas

### Controller
Responsável apenas por receber a requisição HTTP, validar o DTO de entrada (`@Valid`) e delegar. Não contém regra de negócio. Retorna DTOs de resposta, nunca entidades JPA.

### Facade
A criação de um chamado envolve vários passos (validar, classificar, priorizar, atribuir, persistir, notificar, auditar). Em vez de o controller orquestrar tudo isso, ele chama um único método do `TicketFacade`. A Facade reduz o acoplamento entre o controller e o subsistema de criação.

### Service
Onde mora a regra de negócio "pura" de cada agregado (Client, User, SupportTeam, Ticket). Os serviços são beans Spring — portanto, **singletons** gerenciados pelo contêiner, o que satisfaz naturalmente esse padrão sem implementação manual.

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

A **Chain of Responsibility** modela esse pipeline: cada etapa é um handler que processa o chamado e decide passar adiante (`next.handle(ticket)`) ou interromper (ex.: validação falha). Adicionar uma nova etapa significa inserir um handler na cadeia, sem tocar nos demais.

## Ciclo de vida de status

```
ABERTO ──► EM_ANALISE ──► EM_ANDAMENTO ──► FINALIZADO
   ▲                                            │
   └──────────────── (reabertura) ──────────────┘
```

Cada transição de status dispara os **Observers** registrados. Quando o legado de notificação não fala a interface esperada, um **Adapter** faz a ponte.

## Ponto de extensão para IA

A classificação de categoria é feita hoje por regras simples dentro do `CategoryHandler`. Como esse handler depende de uma abstração (`ClassificationStrategy` / interface de classificação), trocar a regra por uma chamada a um modelo de IA é uma substituição de implementação — o resto do pipeline não muda. Esse desacoplamento é deliberado e está descrito no roadmap.

## Princípios seguidos

- **Controllers magros, serviços coesos.** Nenhuma regra de negócio em controller.
- **DTOs na fronteira.** Entidades JPA não cruzam a borda HTTP.
- **Schema versionado.** Toda mudança de banco é uma migration Flyway.
- **Padrões com propósito.** Cada padrão resolve um problema concreto do domínio; nenhum é forçado.