# Getting Started

## Pré-requisitos

- **JDK 21** (`java -version` deve apontar para 21)
- **Docker** e **Docker Compose**
- Git
- _(opcional)_ **Ollama** rodando localmente, apenas se for usar a classificação por IA

## Clonar

```bash
git clone https://github.com/JoaoAzevedo184/Smart-Support-API.git
cd Smart-Support-API
```

## Banco de dados

A aplicação usa PostgreSQL 16. O `docker-compose.yml` já vem pronto e publica o Postgres na porta **5433** do host (para não colidir com um Postgres local na 5432):

```bash
docker compose up -d postgres
```

As credenciais padrão (`smartsupport` / `smartsupport` / db `smartsupport`) podem ser sobrescritas por variáveis de ambiente `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`.

## Configuração da aplicação

O `application.yaml` já está totalmente configurado (datasource, JPA, Flyway, OpenAPI, Actuator, notificações e IA). Todos os valores sensíveis têm default e podem ser sobrescritos por variáveis de ambiente. Os principais:

| Variável | Default | Para quê |
| --- | --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5433/smartsupport` | conexão com o banco |
| `DB_USERNAME` / `DB_PASSWORD` | `smartsupport` | credenciais do banco |
| `SPRING_PROFILES_ACTIVE` | `dev` | profile ativo |
| `NOTIFICATIONS_CHANNEL` | `log` | canal de notificação (`webhook` / `log` / `noop`) |
| `NOTIFICATIONS_WEBHOOK_URL` | _(vazio)_ | URL de destino quando o canal é `webhook` |
| `CLASSIFIER_STRATEGY` | `rules` | classificador de categoria (`rules` / `ai`) |
| `OLLAMA_BASE_URL` | `http://localhost:11434` | servidor Ollama (quando `CLASSIFIER_STRATEGY=ai`) |
| `OLLAMA_MODEL` | `llama3.2` | modelo Ollama a usar |

> `ddl-auto: validate` é proposital: o Hibernate apenas confere se o schema bate com as entidades; quem cria/altera tabelas é o **Flyway**.

## Rodar

```bash
./mvnw spring-boot:run
```

Ou empacotando:

```bash
./mvnw clean package
java -jar target/smart-support-api-0.0.1-SNAPSHOT.jar
```

## Verificar

A API sobe em `http://localhost:8080`.

| Recurso | URL |
| --- | --- |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Health | http://localhost:8080/actuator/health |

Um `GET /actuator/health` retornando `{"status":"UP"}` confirma que app + banco estão de pé.

## Classificação por IA (opcional)

Por padrão a classificação usa regras (`CLASSIFIER_STRATEGY=rules`), sem nenhuma dependência externa. Para habilitar a IA:

**Com Ollama (padrão de IA, local):**

```bash
# tenha o Ollama rodando e o modelo baixado, ex.: ollama pull llama3.2
CLASSIFIER_STRATEGY=ai OLLAMA_MODEL=llama3.2 ./mvnw spring-boot:run
```

**Com Gemini (profile `gemini`):**

```bash
CLASSIFIER_STRATEGY=ai GEMINI_API_KEY=sua-chave \
  ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev,gemini
```

> Ativar o profile `gemini` sem `GEMINI_API_KEY` faz a aplicação falhar ao subir (validação fail-fast do Spring AI).

## Testes

```bash
./mvnw test
```

A suíte é hermética: os testes de integração sobem o PostgreSQL via **Testcontainers** e os testes de IA/webhook usam mocks — nenhuma infraestrutura externa é necessária. Ver [`testing.md`](testing.md).

## Problemas comuns

| Sintoma | Causa provável |
| --- | --- |
| `password authentication failed` | credenciais das variáveis de ambiente ≠ do compose |
| `relation "tickets" does not exist` | Flyway desabilitado ou migrations ausentes |
| `port 5433 already in use` | outro serviço ocupando a porta do compose |
| App falha ao subir com erro de "Google GenAI configuration" | profile `gemini` ativo sem `GEMINI_API_KEY` |
| Classificação por IA sempre cai para regras | Ollama indisponível na `OLLAMA_BASE_URL` ou modelo não baixado |
