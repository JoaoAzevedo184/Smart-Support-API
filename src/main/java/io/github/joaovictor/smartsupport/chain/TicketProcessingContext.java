package io.github.joaovictor.smartsupport.chain;

import io.github.joaovictor.smartsupport.dto.ticket.TicketRequest;
import io.github.joaovictor.smartsupport.entity.Ticket;

/**
 * Objeto de contexto que trafega pela Chain. Carrega o {@link Ticket} em
 * construção e o {@link TicketRequest} original, permitindo aos elos distinguir
 * o que o cliente informou (categoria/prioridade) do que deve ser inferido.
 */
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
