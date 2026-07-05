# Roadmap

O projeto é dividido em fases que vão do esqueleto à API completa com extensão de IA. Cada fase entrega algo funcional e introduz padrões de forma incremental — assim o histórico de commits conta uma boa história em entrevistas.

---

## Fase 0 — Setup ✅

- [x] Projeto gerado no Spring Initializr
- [x] Dependências base (Web, JPA, Validation, Actuator, Flyway, PostgreSQL, Lombok, springdoc)
- [x] Estrutura de pacotes
- [x] Documentação inicial (README + docs)

## Fase 1 — Fundação de dados

- [x] Configurar `application.yaml` (datasource, JPA, Flyway, OpenAPI, Actuator)
- [x] `docker-compose.yml` com PostgreSQL
- [x] Entidades: `Client`, `User`, `SupportTeam`, `Ticket` + enums
- [x] Migrations Flyway (V1–V4)
- [x] Repositórios Spring Data

## Fase 2 — CRUD e contrato

- [x] DTOs de request/response
- [x] Mappers (**MapStruct**)
- [x] CRUD de Client, User, SupportTeam
- [x] `@RestControllerAdvice` para erros padronizados
- [x] OpenAPI exposto e revisado

## Fase 3 — Criação de chamado com padrões

- [x] **Builder** na entidade `Ticket`
- [x] **Factory Method** para tipos de chamado
- [x] **Facade** de criação (`TicketFacade`)
- [x] `POST /tickets` ponta a ponta

## Fase 4 — Pipeline do chamado

- [ ] **Chain of Responsibility**: Validation → Spam → Category → Priority → AssignTeam
- [ ] **Strategy** de prioridade (Urgent/High/Medium/Low)
- [ ] **Template Method**: processadores por categoria (Bug/Billing/Support)
- [ ] Integração do pipeline na Facade

## Fase 5 — Status e notificações

- [ ] Ciclo de vida de status + validação de transições
- [ ] **Observer** (Spring Events): Email, Slack, Audit, Dashboard
- [ ] **Adapter** para o sistema legado de notificação
- [ ] `PUT /tickets/{id}/status` e `POST /tickets/{id}/assign`

## Fase 6 — Ações e relatórios

- [ ] **Command**: Close, Reopen, Assign
- [ ] `GET /tickets/open`
- [ ] `GET /tickets/report` (agregações)

## Fase 7 — Qualidade

- [ ] Testes unitários (JUnit 5 + Mockito) por padrão/serviço
- [ ] Testes de integração com **Testcontainers**
- [ ] **DataFaker** para seed de demo
- [ ] **GitHub Actions** (CI: build + testes)
- [ ] `Dockerfile` da aplicação

## Fase 8 — Extensão de IA

- [ ] Abstrair a classificação de categoria atrás de uma interface
- [ ] Implementação baseada em IA para o `CategoryHandler` (classificação automática)
- [ ] Manter a regra simples como fallback/perfil alternativo

---

## Dica de portfólio

Cada padrão merece pelo menos um commit isolado e legível, com a mensagem deixando claro o padrão (ex.: `feat(chain): pipeline de processamento do chamado`). A tabela "Padrão → Onde foi aplicado" no README é o que o avaliador olha primeiro; mantenha-a sincronizada com o código real à medida que as fases avançam.