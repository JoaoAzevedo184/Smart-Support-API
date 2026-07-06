package io.github.joaovictor.smartsupport.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Simula um sistema legado de notificações, com uma assinatura de método
 * incompatível com a interface moderna {@link NotificationSender}.
 */
@Slf4j
@Component
public class LegacyNotificationSystem {

    public void dispatch(String recipientCode, String payload, int severityCode) {
        log.info("[LEGACY-SYSTEM] dispatch(recipientCode={}, severityCode={}) -> {}",
                recipientCode, severityCode, payload);
    }
}
