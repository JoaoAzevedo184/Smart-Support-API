package io.github.joaovictor.smartsupport.event;

import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.enums.TicketStatus;

public record TicketStatusChangedEvent(
        Ticket ticket,
        TicketStatus previousStatus,
        TicketStatus newStatus
) {
}
