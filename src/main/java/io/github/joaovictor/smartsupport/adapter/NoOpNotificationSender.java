package io.github.joaovictor.smartsupport.adapter;

import io.github.joaovictor.smartsupport.strategy.NotificationChannel;
import io.github.joaovictor.smartsupport.strategy.NotificationChannelSender;
import org.springframework.stereotype.Component;

@Component
public class NoOpNotificationSender implements NotificationChannelSender {

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.NOOP;
    }

    @Override
    public void notify(String recipient, String message) {
        // canal desabilitado intencionalmente: descarta sem registrar
    }
}
