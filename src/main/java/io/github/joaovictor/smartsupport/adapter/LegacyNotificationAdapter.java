package io.github.joaovictor.smartsupport.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adapta a interface moderna {@link NotificationSender} para o
 * sistema legado {@link LegacyNotificationSystem}, cuja assinatura
 * de método é incompatível (recipientCode/payload/severityCode).
 */
@Component
@RequiredArgsConstructor
public class LegacyNotificationAdapter implements NotificationSender {

    private static final int DEFAULT_SEVERITY_CODE = 1;

    private final LegacyNotificationSystem legacyNotificationSystem;

    @Override
    public void notify(String recipient, String message) {
        legacyNotificationSystem.dispatch(recipient, message, DEFAULT_SEVERITY_CODE);
    }
}
