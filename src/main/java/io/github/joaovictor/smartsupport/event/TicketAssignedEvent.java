package io.github.joaovictor.smartsupport.event;

import io.github.joaovictor.smartsupport.entity.SupportTeam;
import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.User;

/** Evento de domínio (Observer) publicado quando um chamado é atribuído. */
public record TicketAssignedEvent(
        Ticket ticket,
        SupportTeam team,
        User user
) {
}
