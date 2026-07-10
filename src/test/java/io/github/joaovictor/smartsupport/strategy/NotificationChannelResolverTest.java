package io.github.joaovictor.smartsupport.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.joaovictor.smartsupport.adapter.LogNotificationAdapter;
import io.github.joaovictor.smartsupport.adapter.NoOpNotificationSender;
import io.github.joaovictor.smartsupport.adapter.WebhookNotificationAdapter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class NotificationChannelResolverTest {

    private final LogNotificationAdapter logAdapter = new LogNotificationAdapter();
    private final NoOpNotificationSender noOpAdapter = new NoOpNotificationSender();
    private final WebhookNotificationAdapter webhookAdapter =
            new WebhookNotificationAdapter(RestClient.builder(), "http://localhost:1", 2000, 1);

    private final List<NotificationChannelSender> senders = List.of(logAdapter, noOpAdapter, webhookAdapter);

    @Test
    void deveSelecionarCanalWebhookQuandoConfigurado() {
        NotificationChannelResolver resolver = new NotificationChannelResolver(senders, "webhook");

        assertThat(resolver.getActive()).isSameAs(webhookAdapter);
    }

    @Test
    void deveSelecionarCanalNoopQuandoConfigurado() {
        NotificationChannelResolver resolver = new NotificationChannelResolver(senders, "noop");

        assertThat(resolver.getActive()).isSameAs(noOpAdapter);
    }

    @Test
    void deveCairParaLogQuandoCanalDesconhecido() {
        NotificationChannelResolver resolver = new NotificationChannelResolver(senders, "algum-canal-invalido");

        assertThat(resolver.getActive()).isSameAs(logAdapter);
    }

    @Test
    void deveUsarLogComoPadraoQuandoNenhumCanalInformado() {
        NotificationChannelResolver resolver = new NotificationChannelResolver(senders, "log");

        assertThat(resolver.getActive()).isSameAs(logAdapter);
    }
}
