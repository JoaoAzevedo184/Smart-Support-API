package io.github.joaovictor.smartsupport.chain;

import io.github.joaovictor.smartsupport.dto.ticket.TicketRequest;
import io.github.joaovictor.smartsupport.entity.Ticket;

public class TicketProcessingContext {

    private final Ticket ticket;
    private final TicketRequest request;

    public TicketProcessingContext(Ticket ticket, TicketRequest request) {
        this.ticket = ticket;
        this.request = request;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public boolean hasExplicitPriority() {
        return request.priority() != null;
    }

    public boolean hasExplicitCategory() {
        return request.category() != null;
    }
}
