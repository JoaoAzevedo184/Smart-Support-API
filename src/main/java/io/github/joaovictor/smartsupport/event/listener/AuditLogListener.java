package io.github.joaovictor.smartsupport.event.listener;

import io.github.joaovictor.smartsupport.event.TicketAssignedEvent;
import io.github.joaovictor.smartsupport.event.TicketStatusChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Observer: registra em log de auditoria cada mudança de status/atribuição. */
@Slf4j
@Component
public class AuditLogListener {

    @EventListener
    public void onStatusChanged(TicketStatusChangedEvent event) {
        log.info("[AUDIT] ticketId={} status {} -> {}",
                event.ticket().getId(), event.previousStatus(), event.newStatus());
    }

    @EventListener
    public void onAssigned(TicketAssignedEvent event) {
        log.info("[AUDIT] ticketId={} atribuído a teamId={} userId={}",
                event.ticket().getId(),
                event.team() != null ? event.team().getId() : null,
                event.user() != null ? event.user().getId() : null);
    }
}
