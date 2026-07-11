package io.github.joaovictor.smartsupport.event;

import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.enums.TicketStatus;

/** Evento de domínio (Observer) publicado quando o status de um chamado muda. */
public record TicketStatusChangedEvent(
        Ticket ticket,
        TicketStatus previousStatus,
        TicketStatus newStatus
) {
}
