package io.github.joaovictor.smartsupport.config;

import io.github.joaovictor.smartsupport.adapter.NotificationSender;
import io.github.joaovictor.smartsupport.strategy.NotificationChannelResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class NotificationConfig {

    /**
     * Expõe como {@link NotificationSender} primário o canal resolvido por
     * {@link NotificationChannelResolver}, para que consumidores (ex.: listeners
     * de evento) permaneçam desacoplados de qual canal está ativo.
     */
    @Bean
    @Primary
    public NotificationSender activeNotificationSender(NotificationChannelResolver resolver) {
        return resolver.getActive();
    }
}
