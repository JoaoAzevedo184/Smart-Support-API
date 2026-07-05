# Getting Started

## Pré-requisitos

- **JDK 21** (`java -version` deve apontar para 21)
- **Docker** e **Docker Compose**
- Git

## Clonar

```bash
git clone https://github.com/JoaoAzevedo184/Smart-Support-API.git
cd Smart-Support-API
```

## Banco de dados

A aplicação usa PostgreSQL. O jeito mais simples é subir via Docker Compose.

`docker-compose.yml` (sugestão):

```yaml
services:
  postgres:
    image: postgres:16-alpine
    container_name: smart-support-db
    environment:
      POSTGRES_DB: smartsupport
      POSTGRES_USER: smartsupport
      POSTGRES_PASSWORD: smartsupport
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

volumes:
  pgdata:
```

```bash
docker compose up -d postgres
```

## Configuração da aplicação

O `application.yaml` atual só define o nome da aplicação. Para conectar ao banco e ativar Flyway/OpenAPI, ele precisa evoluir para algo como:

```yaml
spring:
  application:
    name: smart-support-api
  datasource:
    url: jdbc:postgresql://localhost:5432/smartsupport
    username: smartsupport
    password: smartsupport
  jpa:
    hibernate:
      ddl-auto: validate   # o schema é gerido pelo Flyway, não pelo Hibernate
    open-in-view: false
    properties:
      hibernate.format_sql: true
  flyway:
    enabled: true
    locations: classpath:db/migration

springdoc:
  swagger-ui:
    path: /swagger-ui.html

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

> `ddl-auto: validate` é proposital: o Hibernate apenas confere se o schema bate com as entidades; quem cria/altera tabelas é o Flyway.

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

| Recurso | URL |
| --- | --- |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Health | http://localhost:8080/actuator/health |

Um `GET /actuator/health` retornando `{"status":"UP"}` confirma que app + banco estão de pé.

## Testes

```bash
./mvnw test
```

## Dependências ainda a adicionar ao `pom.xml`

O esqueleto atual já traz Web, JPA, Validation, Actuator, Flyway, PostgreSQL, Lombok e springdoc. Conforme o projeto avança, considere:

```xml
<!-- MapStruct: mapeamento entidade <-> DTO -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.6.3</version>
</dependency>

<!-- DataFaker: popular o banco para demo -->
<dependency>
    <groupId>net.datafaker</groupId>
    <artifactId>datafaker</artifactId>
    <version>2.4.3</version>
</dependency>

<!-- Testcontainers: testes de integração com Postgres real -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
```

> Ao adicionar MapStruct junto com Lombok, inclua o `lombok-mapstruct-binding` no `annotationProcessorPaths` do compiler plugin, senão os mappers não geram corretamente.

## Problemas comuns

| Sintoma | Causa provável |
| --- | --- |
| `password authentication failed` | credenciais do `application.yaml` ≠ do compose |
| `relation "ticket" does not exist` | Flyway desabilitado ou migrations ausentes |
| Mapper MapStruct retorna `null` | falta o `lombok-mapstruct-binding` no annotation processor |
| `port 5432 already in use` | outro Postgres rodando localmente |