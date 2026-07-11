# Modelo de dados

Todas as entidades usam **`UUID`** como chave primária (gerada pela aplicação, `GenerationType.UUID`) e carregam `created_at` / `updated_at` preenchidos automaticamente por callbacks JPA (`@PrePersist` / `@PreUpdate`).

## Entidades

### Client
Cliente que abre chamados.

| Campo | Tipo | Observação |
| --- | --- | --- |
| `id` | UUID | PK |
| `name` | String | obrigatório |
| `email` | String | obrigatório, único |
| `phone` | String | opcional |
| `created_at` | Timestamp | preenchido na criação |
| `updated_at` | Timestamp | atualizado a cada mudança |

### User
Usuário do sistema (atendente, gestor etc.), opcionalmente ligado a uma equipe.

| Campo | Tipo | Observação |
| --- | --- | --- |
| `id` | UUID | PK |
| `name` | String | obrigatório |
| `email` | String | obrigatório, único |
| `password` | String | obrigatório (omitido no `toString`) |
| `role` | enum `UserRole` | `ADMIN`, `MANAGER`, `AGENT` |
| `support_team_id` | FK → SupportTeam | opcional |
| `created_at` / `updated_at` | Timestamp | automáticos |

### SupportTeam
Equipe que recebe chamados atribuídos.

| Campo | Tipo | Observação |
| --- | --- | --- |
| `id` | UUID | PK |
| `name` | String | obrigatório, único |
| `description` | String | opcional |
| `users` | 1..N → User | membros da equipe (`mappedBy = "supportTeam"`) |
| `created_at` / `updated_at` | Timestamp | automáticos |

### Ticket
Entidade central do sistema.

| Campo | Tipo | Observação |
| --- | --- | --- |
| `id` | UUID | PK |
| `title` | String | obrigatório |
| `description` | String | texto longo (`text`) |
| `status` | enum `TicketStatus` | `OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`, `REOPENED` |
| `priority` | enum `TicketPriority` | `LOW`, `MEDIUM`, `HIGH`, `URGENT` |
| `category` | enum `TicketCategory` | `BUG`, `BILLING`, `SUPPORT` |
| `client_id` | FK → Client | quem abriu (obrigatório) |
| `assigned_team_id` | FK → SupportTeam | equipe atribuída (nullable) |
| `assigned_user_id` | FK → User | usuário atribuído (nullable) |
| `created_at` | Timestamp | preenchido na criação |
| `updated_at` | Timestamp | atualizado a cada mudança |
| `closed_at` | Timestamp | preenchido ao mudar para `CLOSED` |

> Ao criar, se `status` não for informado, a entidade assume `OPEN` no `@PrePersist`.

## Relacionamentos

```
Client (1) ───< (N) Ticket >─── (N..1) SupportTeam
                     │
                     └─── (N..1) User        (usuário atribuído)

SupportTeam (1) ───< (N) User                (membros da equipe)
```

- Um **Client** pode ter vários **Tickets**; um **Ticket** pertence a um **Client**.
- Um **Ticket** pode ser atribuído a uma **SupportTeam** e/ou a um **User** (ambos opcionais até a etapa de atribuição).
- Um **User** pode pertencer a uma **SupportTeam**.

## Enums

```java
enum TicketStatus   { OPEN, IN_PROGRESS, RESOLVED, CLOSED, REOPENED }
enum TicketPriority { LOW, MEDIUM, HIGH, URGENT }
enum TicketCategory { BUG, BILLING, SUPPORT }
enum UserRole       { ADMIN, MANAGER, AGENT }
```

### Transições de status permitidas

O `TicketStatus` valida as transições no próprio enum (`canTransitionTo`); uma transição inválida resulta em **HTTP 409**.

| De | Para |
| --- | --- |
| `OPEN` | `IN_PROGRESS`, `CLOSED` |
| `IN_PROGRESS` | `RESOLVED`, `CLOSED` |
| `RESOLVED` | `CLOSED`, `REOPENED` |
| `CLOSED` | `REOPENED` |
| `REOPENED` | `IN_PROGRESS`, `CLOSED` |

## Migrations (Flyway)

O schema é versionado com Flyway em `src/main/resources/db/migration`.

| Versão | Arquivo | Conteúdo |
| --- | --- | --- |
| V1 | `V1__create_clients_table.sql` | tabela `clients` |
| V2 | `V2__create_support_teams_table.sql` | tabela `support_teams` |
| V3 | `V3__create_users_table.sql` | tabela `users` + FK para `support_teams` |
| V4 | `V4__create_tickets_table.sql` | tabela `tickets` + FKs (client, team, user) |

> A tabela de usuários chama-se `users` (não `user`, palavra reservada no PostgreSQL) e é criada **depois** de `support_teams`, por causa da FK `support_team_id`.

## Índices existentes

Criados nas migrations:

- `tickets(client_id)`, `tickets(assigned_team_id)`, `tickets(assigned_user_id)`, `tickets(status)`
- `users(support_team_id)`
- Índices únicos: `clients(email)`, `support_teams(name)`, `users(email)`
