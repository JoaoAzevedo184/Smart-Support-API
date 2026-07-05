# Modelo de dados

## Entidades

### Client
Cliente corporativo que abre chamados.

| Campo | Tipo | Observação |
| --- | --- | --- |
| `id` | Long | PK |
| `name` | String | obrigatório |
| `email` | String | obrigatório, único |
| `company` | String | nome da empresa |

### User
Usuário do sistema (atendente, gestor etc.).

| Campo | Tipo | Observação |
| --- | --- | --- |
| `id` | Long | PK |
| `name` | String | obrigatório |
| `email` | String | obrigatório, único |
| `role` | enum `Role` | `AGENT`, `MANAGER`, `ADMIN` |

### SupportTeam
Equipe que recebe chamados atribuídos.

| Campo | Tipo | Observação |
| --- | --- | --- |
| `id` | Long | PK |
| `name` | String | obrigatório |
| `specialty` | String | área de atuação (ex.: billing, infra) |

### Ticket
Entidade central do sistema.

| Campo | Tipo | Observação |
| --- | --- | --- |
| `id` | Long | PK |
| `title` | String | obrigatório |
| `description` | String | texto longo |
| `status` | enum `Status` | `ABERTO`, `EM_ANALISE`, `EM_ANDAMENTO`, `FINALIZADO` |
| `priority` | enum `Priority` | `URGENT`, `HIGH`, `MEDIUM`, `LOW` |
| `category` | enum `Category` | `BUG`, `BILLING`, `FEATURE_REQUEST`, `SUPPORT` |
| `client_id` | FK → Client | quem abriu |
| `team_id` | FK → SupportTeam | equipe atribuída (nullable até a atribuição) |
| `created_at` | Timestamp | preenchido na criação |
| `updated_at` | Timestamp | atualizado a cada mudança |

## Relacionamentos

```
Client (1) ───< (N) Ticket >─── (N..1) SupportTeam
```

- Um **Client** pode ter vários **Tickets**.
- Um **Ticket** pertence a um **Client**.
- Um **Ticket** pode ser atribuído a uma **SupportTeam** (opcional até passar pela etapa de atribuição).
- **User** e **SupportTeam** são cadastros independentes neste escopo; a associação usuário↔equipe pode ser adicionada como evolução.

## Enums

```java
enum Status   { ABERTO, EM_ANALISE, EM_ANDAMENTO, FINALIZADO }
enum Priority { URGENT, HIGH, MEDIUM, LOW }
enum Category { BUG, BILLING, FEATURE_REQUEST, SUPPORT }
enum Role     { AGENT, MANAGER, ADMIN }
```

## Migrations (Flyway)

O schema é versionado com Flyway. Sugestão de organização inicial em `src/main/resources/db/migration`:

| Versão | Arquivo | Conteúdo |
| --- | --- | --- |
| V1 | `V1__create_client.sql` | tabela `client` |
| V2 | `V2__create_user.sql` | tabela `app_user` (`user` é palavra reservada no Postgres) |
| V3 | `V3__create_support_team.sql` | tabela `support_team` |
| V4 | `V4__create_ticket.sql` | tabela `ticket` + FKs |
| V5 | `V5__seed_data.sql` | dados iniciais (opcional, para demo) |

> Atenção: `user` é palavra reservada no PostgreSQL. Use `app_user` como nome de tabela e mapeie com `@Table(name = "app_user")`.

## Índices recomendados

- `ticket(status)` — consultas de chamados abertos.
- `ticket(client_id)` — chamados por cliente.
- `ticket(created_at)` — relatórios por período.