package io.github.joaovictor.smartsupport.event.listener;

import io.github.joaovictor.smartsupport.event.TicketAssignedEvent;
import io.github.joaovictor.smartsupport.event.TicketStatusChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Observer: publica no canal da equipe (simulado) a cada evento do chamado. */
@Slf4j
@Component
public class SlackNotificationListener {

    @EventListener
    public void onStatusChanged(TicketStatusChangedEvent event) {
        log.info("[SLACK] Postando no canal da equipe: chamado {} agora está {} (era {}).",
                event.ticket().getId(), event.newStatus(), event.previousStatus());
    }

    @EventListener
    public void onAssigned(TicketAssignedEvent event) {
        log.info("[SLACK] Postando no canal da equipe: chamado {} atribuído a time={} usuário={}.",
                event.ticket().getId(),
                event.team() != null ? event.team().getName() : "-",
                event.user() != null ? event.user().getName() : "-");
    }
}
