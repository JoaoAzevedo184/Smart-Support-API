package io.github.joaovictor.smartsupport.strategy;

import io.github.joaovictor.smartsupport.adapter.NotificationSender;

/**
 * {@link NotificationSender} que declara a qual {@link NotificationChannel}
 * pertence, permitindo ao {@link NotificationChannelResolver} indexá-lo e
 * selecioná-lo por configuração (Strategy).
 */
public interface NotificationChannelSender extends NotificationSender {

    NotificationChannel channel();
}
