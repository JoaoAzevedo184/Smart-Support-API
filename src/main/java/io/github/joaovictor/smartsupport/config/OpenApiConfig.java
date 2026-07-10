package io.github.joaovictor.smartsupport.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/** Metadados globais da documentação OpenAPI/Swagger. */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Smart Support API",
                version = "v1",
                description = "API de gestão de chamados de suporte, com pipeline de processamento e notificações."
        )
)
public class OpenApiConfig {
}
