package io.github.joaovictor.smartsupport.config;

import io.github.joaovictor.smartsupport.classifier.TicketClassifier;
import io.github.joaovictor.smartsupport.classifier.TicketClassifierResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ClassifierConfig {

    /**
     * Expõe como {@link TicketClassifier} primário o modo resolvido por
     * {@link TicketClassifierResolver}, mantendo a Chain of Responsibility
     * desacoplada de qual classificador está ativo.
     */
    @Bean
    @Primary
    public TicketClassifier activeTicketClassifier(TicketClassifierResolver resolver) {
        return resolver.getActive();
    }
}
