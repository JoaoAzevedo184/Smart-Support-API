package io.github.joaovictor.smartsupport.strategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Seleciona a implementação de {@link NotificationChannelSender} ativa
 * conforme {@code app.notifications.channel}. Canal desconhecido ou
 * ausente cai para {@link NotificationChannel#LOG}.
 */
@Component
public class NotificationChannelResolver {

    private final Map<NotificationChannel, NotificationChannelSender> sendersByChannel;
    private final NotificationChannel configuredChannel;

    public NotificationChannelResolver(
            List<NotificationChannelSender> senders,
            @Value("${app.notifications.channel:log}") String configuredChannel) {
        this.sendersByChannel = senders.stream()
                .collect(Collectors.toMap(NotificationChannelSender::channel, Function.identity()));
        this.configuredChannel = parseChannel(configuredChannel);
    }

    public NotificationChannelSender getActive() {
        return sendersByChannel.getOrDefault(configuredChannel, sendersByChannel.get(NotificationChannel.LOG));
    }

    private static NotificationChannel parseChannel(String raw) {
        try {
            return NotificationChannel.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            return NotificationChannel.LOG;
        }
    }
}
