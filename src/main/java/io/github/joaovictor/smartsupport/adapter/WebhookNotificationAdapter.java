package io.github.joaovictor.smartsupport.adapter;

import io.github.joaovictor.smartsupport.strategy.NotificationChannel;
import io.github.joaovictor.smartsupport.strategy.NotificationChannelSender;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Traduz eventos de domínio (via {@link NotificationSender#notify}) em um POST HTTP
 * para uma URL configurável ({@code app.notifications.webhook-url}). Falhas de rede
 * são absorvidas (com retry simples) para não interromper o fluxo principal do chamado.
 */
@Slf4j
@Component
public class WebhookNotificationAdapter implements NotificationChannelSender {

    private static final String EVENT_TYPE = "ticket.notification";

    // ===== Configuração (injetada por properties) =====
    private final RestClient restClient;
    private final String webhookUrl;
    private final int maxAttempts;

    // ===== Construção (monta o RestClient com timeouts) =====
    public WebhookNotificationAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${app.notifications.webhook-url:}") String webhookUrl,
            @Value("${app.notifications.webhook-timeout-ms:2000}") int timeoutMs,
            @Value("${app.notifications.webhook-retry-attempts:2}") int maxAttempts) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutMs);
        requestFactory.setReadTimeout(timeoutMs);
        this.restClient = restClientBuilder.requestFactory(requestFactory).build();
        this.webhookUrl = webhookUrl;
        this.maxAttempts = Math.max(1, maxAttempts);
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.WEBHOOK;
    }

    // ===== Envio (traduz para POST HTTP; sem URL, cai para log) =====
    @Override
    public void notify(String recipient, String message) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("[WEBHOOK] Nenhuma URL configurada (app.notifications.webhook-url); "
                    + "notificação registrada apenas em log: destinatario={} mensagem={}", recipient, message);
            return;
        }

        WebhookPayload payload = new WebhookPayload(EVENT_TYPE, recipient, message, Instant.now());
        sendWithRetry(payload);
    }

    // ===== Resiliência (retry simples; falha não interrompe o fluxo) =====
    private void sendWithRetry(WebhookPayload payload) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                restClient.post()
                        .uri(webhookUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(payload)
                        .retrieve()
                        .toBodilessEntity();
                return;
            } catch (RestClientException ex) {
                if (attempt == maxAttempts) {
                    log.error("[WEBHOOK] Falha ao enviar notificação após {} tentativa(s), "
                            + "descartando sem interromper o fluxo: {}", attempt, ex.getMessage());
                } else {
                    log.warn("[WEBHOOK] Falha ao enviar notificação (tentativa {}/{}): {}",
                            attempt, maxAttempts, ex.getMessage());
                }
            }
        }
    }
}
