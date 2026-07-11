package io.github.joaovictor.smartsupport.adapter;

import java.time.Instant;

/** Corpo JSON enviado pelo {@link WebhookNotificationAdapter} no POST. */
public record WebhookPayload(
        String event,
        String ticketId,
        String message,
        Instant timestamp
) {
}
