# 🎫 Smart Support API

> REST API para gestão inteligente de chamados de suporte, construída com **Java 21 + Spring Boot 3.5** e estruturada em torno da aplicação prática de **Design Patterns (GoF)**.

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.16-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Flyway](https://img.shields.io/badge/Flyway-migrations-CC0200?logo=flyway&logoColor=white)](https://flywaydb.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![Tests](https://img.shields.io/badge/tests-Testcontainers-40B5A4?logo=testcontainers&logoColor=white)](https://testcontainers.com/)
[![License](https://img.shields.io/badge/license-MIT-lightgrey)](LICENSE)

---

## 📋 Sobre o projeto

A **Smart Support API** simula o núcleo de um sistema de _help desk_ corporativo: clientes abrem chamados, cada chamado percorre um pipeline (validação, classificação, priorização, atribuição, notificação e auditoria) e muda de status ao longo do seu ciclo de vida.

O objetivo do projeto vai além do CRUD. Ele foi desenhado para aplicar **dez padrões de projeto** em pontos onde eles resolvem problemas reais, evitando exemplos artificiais. O domínio de gestão de chamados foi escolhido justamente porque é comum em empresas e gera situações naturais para Factory, Strategy, Chain of Responsibility, Observer e os demais.

A arquitetura também deixa preparado um ponto de extensão para **classificação automática de chamados via IA**, sem acoplar essa decisão ao restante do fluxo.

---

## 🧩 Padrões de projeto aplicados

| Padrão | Onde foi aplicado | Pacote |
| --- | --- | --- |
| 🏭 **Factory Method** | Criação de diferentes tipos de chamado (bug, cobrança, feature) | `factory` |
| 🧱 **Builder** | Construção da entidade `Ticket`, que tem muitos atributos | `builder` |
| 🎯 **Strategy** | Cálculo de prioridade, seleção do canal de notificação (webhook/log/no-op) e do modo de classificação (regras/IA) | `strategy`, `classifier` |
| ⛓️ **Chain of Responsibility** | Pipeline de processamento do chamado | `chain` |
| 👀 **Observer** | Notificações disparadas quando o status muda (Spring Events + listeners) | `event` |
| 🎭 **Facade** | Orquestração da criação de chamados | `facade` |
| 🔌 **Adapter** | Integração com sistema legado e com webhook configurável de notificações | `adapter` |
| 📐 **Template Method** | Fluxo comum a diferentes categorias de chamado | `template` |
| ⚡ **Command** | Ações como fechar, reabrir e atribuir chamados | `command` |
| 🧍 **Singleton** | Serviços únicos gerenciados pelo contêiner Spring | (beans) |

> 📖 Cada padrão está documentado em detalhe, com motivação e diagrama, em [`docs/design-patterns.md`](docs/design-patterns.md).

---

## 🛠️ Tecnologias

- **Java 21**
- **Spring Boot 3.5.16** (Web, Data JPA, Validation, Actuator)
- **PostgreSQL 16**
- **Flyway** — versionamento do schema do banco
- **Lombok** — redução de boilerplate
- **springdoc-openapi** — documentação OpenAPI / Swagger UI
- **Docker / Docker Compose** — ambiente local
- **Spring AI** — classificação de chamados via LLM (Ollama por padrão, Gemini via profile)
- **JUnit 5 + Mockito + Testcontainers** — testes herméticos, sem dependência de banco externo

---

## 🚀 Como executar

### Pré-requisitos

- JDK 21
- Docker e Docker Compose (recomendado), ou um PostgreSQL local

### Subindo com Docker Compose

```bash
git clone https://github.com/JoaoAzevedo184/Smart-Support-API.git
cd Smart-Support-API
docker compose up -d
./mvnw spring-boot:run
```

### Executando localmente (sem Docker para a app)

```bash
# sobe apenas o banco
docker compose up -d postgres

# roda a aplicação
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`.

| Recurso | URL |
| --- | --- |
| 📘 Swagger UI | http://localhost:8080/swagger-ui.html |
| 📄 OpenAPI JSON | http://localhost:8080/v3/api-docs |
| ❤️ Health (Actuator) | http://localhost:8080/actuator/health |

> 📖 Guia completo em [`docs/getting-started.md`](docs/getting-started.md).

---

## 🔔 Notificações

O canal de notificação (Observer → Adapter) é configurável via variáveis de ambiente, sem precisar recompilar:

| Variável | Padrão | Descrição |
| --- | --- | --- |
| `NOTIFICATIONS_CHANNEL` | `log` | Canal ativo: `webhook`, `log` ou `noop` |
| `NOTIFICATIONS_WEBHOOK_URL` | _(vazio)_ | URL de destino do `POST` quando o canal é `webhook` |
| `NOTIFICATIONS_WEBHOOK_TIMEOUT_MS` | `2000` | Timeout de conexão/leitura da chamada HTTP |
| `NOTIFICATIONS_WEBHOOK_RETRY_ATTEMPTS` | `2` | Tentativas antes de descartar a notificação (sem interromper o fluxo) |

Se o canal for `webhook` e nenhuma URL estiver configurada, a notificação cai automaticamente para o modo `log`.

---

## 🤖 Classificação de chamados via IA

Quando o cliente não informa a `category` do chamado no `POST /api/tickets`, o `CategoryHandler` classifica automaticamente a partir do título e da descrição (Chain of Responsibility → Strategy → Adapter para o LLM). Se a categoria for informada explicitamente, ela é respeitada e a classificação não roda.

| Variável | Padrão | Descrição |
| --- | --- | --- |
| `CLASSIFIER_STRATEGY` | `rules` | Classificador ativo: `rules` (baseline por palavra-chave, sem IA) ou `ai` |
| `OLLAMA_BASE_URL` | `http://localhost:11434` | URL do servidor Ollama (provedor padrão de IA) |
| `OLLAMA_MODEL` | `llama3.2` | Modelo Ollama a usar (ajuste para o modelo instalado localmente) |

Por padrão, `CLASSIFIER_STRATEGY=rules`, então nenhuma chamada de IA acontece a menos que seja explicitamente habilitada — mantendo dev/CI livres de dependência externa. Qualquer falha da IA (timeout, indisponibilidade, resposta não reconhecida) cai automaticamente para o classificador por regras, sem interromper a criação do chamado.

**Trocando o provedor de IA para Gemini** (perfil `gemini`, definido em `application-gemini.yaml`):

```bash
CLASSIFIER_STRATEGY=ai GEMINI_API_KEY=sua-chave ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev,gemini
```

> ⚠️ Ativar o profile `gemini` sem definir `GEMINI_API_KEY` faz a aplicação falhar ao subir (validação fail-fast do Spring AI) — o profile só deve ser ativado quando a chave estiver disponível.

---

## 🧪 Testes

A suíte de testes é **100% hermética**: os testes de integração sobem o próprio PostgreSQL via **Testcontainers**, sem depender de nenhum banco pré-existente na máquina ou no CI.

```bash
./mvnw test
```

Isso significa que o mesmo comando roda idêntico na sua máquina e no pipeline de CI — sem configuração de infraestrutura externa.

---

## 📡 Principais endpoints

| Método | Rota | Descrição |
| --- | --- | --- |
| `POST` | `/api/tickets` | Cria um chamado |
| `PUT` | `/api/tickets/{id}/status` | Atualiza o status |
| `POST` | `/api/tickets/{id}/assign` | Atribui equipe/usuário |
| `POST` | `/api/tickets/{id}/close` | Fecha um chamado |
| `POST` | `/api/tickets/{id}/reopen` | Reabre um chamado |
| `GET` | `/api/tickets/open` | Lista chamados abertos |
| `GET` | `/api/tickets/report` | Relatório agregado |

> Cadastros auxiliares expõem CRUD padrão em `/api/clients`, `/api/users` e `/api/support-teams`.

> 📖 Contrato detalhado em [`docs/api.md`](docs/api.md).

---

## 📂 Estrutura do projeto

```
smart-support-api
├── docs/                         # documentação do projeto
├── src/main/java/io/github/joaovictor/smartsupport
│   ├── config        # configurações (OpenAPI, beans)
│   ├── controller    # endpoints REST
│   ├── dto           # objetos de transporte
│   ├── entity        # entidades JPA
│   ├── repository    # repositórios Spring Data
│   ├── service       # regras de negócio
│   ├── exception     # tratamento de erros
│   ├── mapper        # conversão entidade <-> DTO
│   ├── factory       # Factory Method
│   ├── builder       # Builder
│   ├── strategy      # Strategy
│   ├── classifier    # classificação de chamados (regras + IA via Spring AI)
│   ├── event         # eventos de domínio + listeners (Observer via Spring Events)
│   ├── chain         # Chain of Responsibility
│   ├── facade        # Facade
│   ├── template      # Template Method
│   ├── command       # Command
│   ├── adapter       # Adapter
│   └── util
├── src/main/resources
│   ├── application.yaml
│   └── db/migration  # scripts Flyway
├── src/test/java/...             # testes (JUnit 5 + Testcontainers)
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

---

## 📚 Documentação

| Documento | Conteúdo |
| --- | --- |
| [`docs/architecture.md`](docs/architecture.md) | Visão arquitetural, camadas e fluxo do chamado |
| [`docs/design-patterns.md`](docs/design-patterns.md) | Cada padrão, motivação e diagrama |
| [`docs/data-model.md`](docs/data-model.md) | Modelo de dados e entidades |
| [`docs/api.md`](docs/api.md) | Contrato REST detalhado |
| [`docs/getting-started.md`](docs/getting-started.md) | Setup, execução e configuração |
| [`docs/testing.md`](docs/testing.md) | Estratégia de testes e Testcontainers |
| [`docs/roadmap.md`](docs/roadmap.md) | Fases de implementação |

---

## 🗺️ Roadmap resumido

- [x] Setup inicial (Spring Initializr, Flyway, OpenAPI)
- [x] Modelagem das entidades e migrations
- [x] CRUD de Client, User, SupportTeam e Ticket
- [x] Pipeline do chamado (Chain of Responsibility + Strategy)
- [x] Notificações por status (Observer + Adapter)
- [x] Facade de criação e Commands de ações
- [x] Cobertura de testes hermética (Testcontainers)
- [x] Notificação via webhook configurável (Adapter + Strategy)
- [x] Extensão de IA para classificação (Strategy + Adapter, Ollama padrão / Gemini via profile)

> 📖 Detalhamento em [`docs/roadmap.md`](docs/roadmap.md).

---

## 👤 Autor

**João Victor Azevedo** — [@JoaoAzevedo184](https://github.com/JoaoAzevedo184)

## 📄 Licença

Distribuído sob a licença MIT.