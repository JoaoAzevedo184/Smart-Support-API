# 🗺️ Roadmap

O projeto é dividido em fases que vão do esqueleto à API completa com extensão de IA. Cada fase entrega algo funcional e introduz padrões de forma incremental — assim o histórico de commits conta uma boa história em entrevistas.

---

## ✅ Fase 1 — Fundação

- [x] Setup inicial via Spring Initializr (Web, Data JPA, Validation, Actuator)
- [x] Configuração do Flyway para versionamento do schema
- [x] Configuração do springdoc-openapi (Swagger UI)
- [x] Docker Compose com PostgreSQL 16
- [x] Perfis de ambiente (`dev`) e configuração externalizada

## ✅ Fase 2 — Modelagem e persistência

- [x] Entidades JPA: `Client`, `User`, `SupportTeam`, `Ticket`
- [x] Migrations Flyway para todas as tabelas
- [x] Enums de domínio: `TicketStatus`, `TicketCategory`, `TicketPriority`
- [x] **Builder** na construção da entidade `Ticket`

## ✅ Fase 3 — CRUD e contrato REST

- [x] CRUD de `Client`, `User`, `SupportTeam` e `Ticket`
- [x] DTOs de request/response com Bean Validation
- [x] **Factory Method** para criação de tipos de chamado
- [x] Tratamento centralizado de exceções (`@RestControllerAdvice`)
- [x] Documentação OpenAPI dos endpoints

## ✅ Fase 4 — Pipeline do chamado

- [x] **Chain of Responsibility**: Validation → Spam → Category → Priority → AssignTeam
- [x] **Strategy** de prioridade (Urgent/High/Medium/Low)
- [x] **Template Method**: processadores por categoria (Bug/Billing/Support)
- [x] Integração do pipeline na Facade
- [x] **Facade** de criação de chamados
- [x] **Command** para ações (fechar, reabrir, atribuir)
- [x] **Observer** para reação a mudanças de status
- [x] Suíte de testes hermética com **Testcontainers** (integração ponta a ponta)
- [x] Pipeline de CI verde (testes rodam sem banco externo)

## ✅ Fase 5 — Notificação via webhook configurável

> Reposiciona a antiga ideia de "integração com Slack". Em vez de acoplar a um provedor específico, o sistema envia notificações para **qualquer URL configurável** via webhook — mais genérico, mais desacoplado e sem depender de credenciais de terceiros.

> **Base já concluída na Fase 4:** o padrão **Observer** (Spring Events em `event/`, com listeners de Email, Slack, Auditoria e Dashboard) e o padrão **Adapter** (`NotificationSender` + `LegacyNotificationAdapter` para o sistema legado) já estavam implementados. Esta fase não reintroduz esses padrões — ela adiciona uma nova implementação de `NotificationSender` voltada a webhook, reaproveitando a interface existente.

- [x] **Adapter**: `WebhookNotificationAdapter` — nova implementação de `NotificationSender` que traduz eventos de domínio em payload HTTP
- [x] **Strategy** de notificação: `NotificationChannelResolver` seleciona o canal (`webhook`, `log`, `noop`) por configuração (`app.notifications.channel`), exposto como bean `@Primary` de `NotificationSender`
- [x] Envio de `POST` para URL configurável (`app.notifications.webhook-url`)
- [x] Payload JSON com evento, chamado (`ticketId`) e timestamp
- [x] Modo `log` como fallback quando nenhuma URL está configurada (canal padrão) ou quando o canal informado é desconhecido
- [x] Tratamento resiliente de falha (timeout configurável, retry simples, não quebra o fluxo principal)
- [x] Testes: `MockWebServer` (OkHttp) valida o POST, o payload JSON, o fallback sem URL e a resiliência a erro 500, sem servidor real

**Por que webhook e não Slack?**
Um webhook genérico demonstra o mesmo padrão de desacoplamento (Adapter + Strategy) sem prender o projeto a uma API externa que exigiria token, workspace e configuração de terceiros. Qualquer consumidor — Slack, Discord, n8n, um endpoint próprio — pode receber a notificação apenas configurando a URL. Isso é mais próximo de como sistemas reais expõem integrações.

## 🔮 Fase 6 — Extensão de IA para classificação

> Ponto de extensão preparado desde o início: classificar automaticamente a categoria e a prioridade do chamado a partir do texto, sem reescrever o pipeline.

- [ ] Interface `TicketClassifier` (contrato de classificação)
- [ ] Implementação baseada em regras como baseline (`RuleBasedClassifier`)
- [ ] Implementação com LLM (`AiTicketClassifier`) via Spring AI
- [ ] **Strategy** para alternar entre classificador por regras e por IA
- [ ] Injeção do classificador no elo `Category` da Chain of Responsibility
- [ ] Fallback automático para regras quando a IA está indisponível
- [ ] Testes com classificador mockado (sem chamar LLM real no CI)

**Design:** o classificador entra como mais uma implementação por trás de uma interface já existente no pipeline. Nenhum outro componente muda — é a validação prática de que os padrões aplicados nas fases anteriores realmente deixaram o sistema aberto para extensão (OCP).

---

## 📌 Notas de arquitetura

- Cada fase mantém a suíte de testes verde antes de avançar.
- Nenhuma fase depende de infraestrutura externa real: banco via Testcontainers, webhook via mock server, IA via mock no CI.
- O histórico de commits acompanha as fases, de modo que a evolução do projeto seja legível para quem revisa o repositório.