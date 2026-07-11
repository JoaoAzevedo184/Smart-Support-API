# Estratégia de testes

A suíte é **100% hermética**: roda idêntica na máquina do dev e no CI, sem depender de banco, LLM ou serviço HTTP externo. Basta:

```bash
./mvnw test
```

## Camadas de teste

| Tipo | Como | Exemplos |
| --- | --- | --- |
| Unitário (padrões) | POJOs/Mockito, sem contexto Spring | `PriorityResolverTest`, `TicketFactoryProviderTest`, `RuleBasedClassifierTest`, `TicketProcessingChainTest` |
| Unitário (serviços/facade) | Mockito (`@Mock` + `@InjectMocks`) | `ClientServiceTest`, `TicketFacadeTest` |
| Integração ponta a ponta | `@SpringBootTest` + Testcontainers + `TestRestTemplate` | `TicketLifecycleIntegrationTest` |
| Contexto | sobe o contexto Spring completo | `SmartSupportApiApplicationTests` |

## Banco real via Testcontainers

Os testes de integração não usam H2 nem um Postgres pré-instalado: eles sobem um container **PostgreSQL 16** descartável. A configuração fica em `TestcontainersConfig`:

```java
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:16-alpine");
    }
}
```

O `@ServiceConnection` faz o Spring Boot injetar automaticamente URL/credenciais do container no datasource — sem `@DynamicPropertySource` manual. O Flyway aplica as migrations no container ao subir o contexto, então os testes exercitam o mesmo schema de produção.

`TicketLifecycleIntegrationTest` cobre o fluxo completo por HTTP: cria cliente → abre chamado → muda status → fecha → consulta relatório, validando status e `closedAt` a cada passo.

## Sem chamadas externas reais

- **IA**: `AiTicketClassifierTest` mocka totalmente o `ChatClient` do Spring AI — valida o parsing da resposta e o fallback por regras, sem chamar Ollama/Gemini. No contexto de integração, o classificador ativo é o de regras (`app.classifier.strategy=rules`, default), então nenhum LLM é acionado.
- **Webhook**: `WebhookNotificationAdapterTest` usa **MockWebServer** (OkHttp) para validar o `POST`, o payload JSON, o fallback sem URL e a resiliência a erro HTTP 500 — sem servidor real.

## Por que isso importa

O mesmo comando (`./mvnw test`) roda sem nenhum setup de infraestrutura. O CI não precisa provisionar banco, chave de API ou endpoint de webhook — o que torna o pipeline reproduzível e rápido de configurar.
