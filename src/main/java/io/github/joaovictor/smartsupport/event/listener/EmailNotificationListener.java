package io.github.joaovictor.smartsupport.event.listener;

import io.github.joaovictor.smartsupport.event.TicketAssignedEvent;
import io.github.joaovictor.smartsupport.event.TicketStatusChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailNotificationListener {

    @EventListener
    public void onStatusChanged(TicketStatusChangedEvent event) {
        log.info("[EMAIL] Chamado {} mudou de status: {} -> {}. Notificando cliente {} por e-mail.",
                event.ticket().getId(), event.previousStatus(), event.newStatus(),
                event.ticket().getClient().getId());
    }

    @EventListener
    public void onAssigned(TicketAssignedEvent event) {
        log.info("[EMAIL] Chamado {} foi atribuído. Notificando cliente {} por e-mail.",
                event.ticket().getId(), event.ticket().getClient().getId());
    }
}
