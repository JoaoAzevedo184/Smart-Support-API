package io.github.joaovictor.smartsupport.strategy;

import io.github.joaovictor.smartsupport.adapter.NotificationSender;

public interface NotificationChannelSender extends NotificationSender {

    NotificationChannel channel();
}
