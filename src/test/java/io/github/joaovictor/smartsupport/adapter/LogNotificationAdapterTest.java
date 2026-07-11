package io.github.joaovictor.smartsupport.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.github.joaovictor.smartsupport.strategy.NotificationChannel;
import org.junit.jupiter.api.Test;

class LogNotificationAdapterTest {

    private final LogNotificationAdapter adapter = new LogNotificationAdapter();

    @Test
    void deveExporCanalLog() {
        assertThat(adapter.channel()).isEqualTo(NotificationChannel.LOG);
    }

    @Test
    void naoDeveLancarExcecaoAoNotificar() {
        assertThatCode(() -> adapter.notify("ticket-1", "mensagem")).doesNotThrowAnyException();
    }
}
