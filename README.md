# Smart Support API

> REST API para gestão inteligente de chamados de suporte, construída com **Java 21 + Spring Boot 3.5** e estruturada em torno da aplicação prática de **Design Patterns (GoF)**.

[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.16-green)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/license-MIT-lightgrey)](LICENSE)

---

## Sobre o projeto

A **Smart Support API** simula o núcleo de um sistema de help desk corporativo: clientes abrem chamados, cada chamado percorre um pipeline (validação, classificação, priorização, atribuição, notificação e auditoria) e muda de status ao longo do seu ciclo de vida.

O objetivo do projeto vai além do CRUD. Ele foi desenhado para aplicar **dez padrões de projeto** em pontos onde eles resolvem problemas reais, evitando exemplos artificiais. O domínio de gestão de chamados foi escolhido justamente porque é comum em empresas e gera situações naturais para Factory, Strategy, Chain of Responsibility, Observer e os demais.

A arquitetura também deixa preparado um ponto de extensão para **classificação automática de chamados via IA**, sem acoplar essa decisão ao restante do fluxo.

---

## Padrões de projeto aplicados

| Padrão | Onde foi aplicado | Pacote |
| --- | --- | --- |
| **Factory Method** | Criação de diferentes tipos de chamado (bug, cobrança, feature) | `factory` |
| **Builder** | Construção da entidade `Ticket`, que tem muitos atributos | `builder` |
| **Strategy** | Cálculo de prioridade conforme regras distintas | `strategy` |
| **Chain of Responsibility** | Pipeline de processamento do chamado | `chain` |
| **Observer** | Notificações disparadas quando o status muda | `observer` |
| **Facade** | Orquestração da criação de chamados | `facade` |
| **Adapter** | Integração com sistema legado de notificações | `adapter` |
| **Template Method** | Fluxo comum a diferentes categorias de chamado | `template` |
| **Command** | Ações como fechar, reabrir e atribuir chamados | `command` |
| **Singleton** | Serviços únicos gerenciados pelo contêiner Spring | (beans) |

> Cada padrão está documentado em detalhe, com motivação e diagrama, em [`docs/design-patterns.md`](docs/design-patterns.md).

---

## Tecnologias

- **Java 21**
- **Spring Boot 3.5.16** (Web, Data JPA, Validation, Actuator)
- **PostgreSQL 16**
- **Flyway** — versionamento do schema do banco
- **Lombok** — redução de boilerplate
- **springdoc-openapi** — documentação OpenAPI / Swagger UI
- **Docker / Docker Compose** — ambiente local
- **JUnit 5 + Mockito** — testes

---

## Como executar

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
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Health (Actuator) | http://localhost:8080/actuator/health |

> Guia completo em [`docs/getting-started.md`](docs/getting-started.md).

---

## Principais endpoints

| Método | Rota | Descrição |
| --- | --- | --- |
| `POST` | `/tickets` | Cria um chamado |
| `GET` | `/tickets` | Lista chamados |
| `GET` | `/tickets/{id}` | Detalha um chamado |
| `PUT` | `/tickets/{id}/status` | Atualiza o status |
| `POST` | `/tickets/{id}/assign` | Atribui uma equipe |
| `GET` | `/tickets/open` | Lista chamados abertos |
| `GET` | `/tickets/report` | Relatório agregado |

> Contrato detalhado em [`docs/api.md`](docs/api.md).

---

## Estrutura do projeto

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
│   ├── observer      # Observer
│   ├── chain         # Chain of Responsibility
│   ├── facade        # Facade
│   ├── template      # Template Method
│   ├── command       # Command
│   ├── adapter       # Adapter
│   └── util
├── src/main/resources
│   ├── application.yaml
│   └── db/migration  # scripts Flyway
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

---

## Documentação

| Documento | Conteúdo |
| --- | --- |
| [`docs/architecture.md`](docs/architecture.md) | Visão arquitetural, camadas e fluxo do chamado |
| [`docs/design-patterns.md`](docs/design-patterns.md) | Cada padrão, motivação e diagrama |
| [`docs/data-model.md`](docs/data-model.md) | Modelo de dados e entidades |
| [`docs/api.md`](docs/api.md) | Contrato REST detalhado |
| [`docs/getting-started.md`](docs/getting-started.md) | Setup, execução e configuração |
| [`docs/roadmap.md`](docs/roadmap.md) | Fases de implementação |

---

## Roadmap resumido

- [x] Setup inicial (Spring Initializr, Flyway, OpenAPI)
- [ ] Modelagem das entidades e migrations
- [ ] CRUD de Client, User, SupportTeam e Ticket
- [ ] Pipeline do chamado (Chain of Responsibility + Strategy)
- [ ] Notificações por status (Observer + Adapter)
- [ ] Facade de criação e Commands de ações
- [ ] Cobertura de testes
- [ ] Extensão de IA para classificação

> Detalhamento em [`docs/roadmap.md`](docs/roadmap.md).

---

## Autor

**João Victor Azevedo** — [@JoaoAzevedo184](https://github.com/JoaoAzevedo184)

## Licença

Distribuído sob a licença MIT.