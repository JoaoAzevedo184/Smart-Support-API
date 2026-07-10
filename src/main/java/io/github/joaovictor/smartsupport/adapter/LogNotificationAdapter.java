package io.github.joaovictor.smartsupport.adapter;

import io.github.joaovictor.smartsupport.strategy.NotificationChannel;
import io.github.joaovictor.smartsupport.strategy.NotificationChannelSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Canal padrão: registra a notificação em log. Usado como fallback quando
 * nenhum canal externo está configurado.
 */
@Slf4j
@Component
public class LogNotificationAdapter implements NotificationChannelSender {

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.LOG;
    }

    @Override
    public void notify(String recipient, String message) {
        log.info("[NOTIFICATION-LOG] destinatario={} mensagem={}", recipient, message);
    }
}
