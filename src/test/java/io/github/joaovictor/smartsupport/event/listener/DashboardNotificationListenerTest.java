package io.github.joaovictor.smartsupport.event.listener;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;

import io.github.joaovictor.smartsupport.adapter.NotificationSender;
import io.github.joaovictor.smartsupport.entity.Client;
import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.enums.TicketStatus;
import io.github.joaovictor.smartsupport.event.TicketStatusChangedEvent;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardNotificationListenerTest {

    @Mock
    private NotificationSender notificationSender;

    @Test
    void deveNotificarSistemaLegadoAoMudarStatus() {
        DashboardNotificationListener listener = new DashboardNotificationListener(notificationSender);
        Ticket ticket = Ticket.builder().id(UUID.randomUUID()).client(Client.builder().build()).build();

        listener.onStatusChanged(new TicketStatusChangedEvent(ticket, TicketStatus.OPEN, TicketStatus.CLOSED));

        verify(notificationSender).notify(
                org.mockito.ArgumentMatchers.eq(ticket.getId().toString()),
                contains("CLOSED"));
    }
}
