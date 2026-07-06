package io.github.joaovictor.smartsupport.template;

import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;

public abstract class CategoryProcessor {

    public final void process(Ticket ticket) {
        validate(ticket);
        enrich(ticket);
    }

    public abstract TicketCategory category();

    protected void validate(Ticket ticket) {
    }

    protected void enrich(Ticket ticket) {
    }
}
