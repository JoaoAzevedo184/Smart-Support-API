package io.github.joaovictor.smartsupport.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.github.joaovictor.smartsupport.strategy.NotificationChannel;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class WebhookNotificationAdapterTest {

    private MockWebServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void deveEnviarPostComPayloadJsonParaUrlConfigurada() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(200));
        String url = server.url("/webhook").toString();

        WebhookNotificationAdapter adapter = new WebhookNotificationAdapter(
                RestClient.builder(), url, 2000, 2);

        adapter.notify("ticket-123", "Chamado mudou de status");

        RecordedRequest recorded = server.takeRequest(1, TimeUnit.SECONDS);
        assertThat(recorded).isNotNull();
        assertThat(recorded.getMethod()).isEqualTo("POST");
        assertThat(recorded.getHeader("Content-Type")).contains("application/json");
        String body = recorded.getBody().readUtf8();
        assertThat(body).contains("\"event\":\"ticket.notification\"")
                .contains("\"ticketId\":\"ticket-123\"")
                .contains("\"message\":\"Chamado mudou de status\"")
                .contains("\"timestamp\"");
    }

    @Test
    void deveApenasLogarQuandoNenhumaUrlEstaConfigurada() {
        WebhookNotificationAdapter adapter = new WebhookNotificationAdapter(
                RestClient.builder(), "", 2000, 2);

        assertThatCode(() -> adapter.notify("ticket-123", "mensagem")).doesNotThrowAnyException();
        assertThat(server.getRequestCount()).isZero();
    }

    @Test
    void naoDeveLancarExcecaoQuandoServidorFalha() {
        server.enqueue(new MockResponse().setResponseCode(500));
        server.enqueue(new MockResponse().setResponseCode(500));
        String url = server.url("/webhook").toString();

        WebhookNotificationAdapter adapter = new WebhookNotificationAdapter(
                RestClient.builder(), url, 2000, 2);

        assertThatCode(() -> adapter.notify("ticket-123", "mensagem")).doesNotThrowAnyException();
        assertThat(server.getRequestCount()).isEqualTo(2);
    }

    @Test
    void deveExporCanalWebhook() {
        WebhookNotificationAdapter adapter = new WebhookNotificationAdapter(
                RestClient.builder(), "", 2000, 2);

        assertThat(adapter.channel()).isEqualTo(NotificationChannel.WEBHOOK);
    }
}
