package io.github.joaovictor.smartsupport.adapter;

import java.time.Instant;

public record WebhookPayload(
        String event,
        String ticketId,
        String message,
        Instant timestamp
) {
}
