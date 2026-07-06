package io.github.joaovictor.smartsupport.factory;

import io.github.joaovictor.smartsupport.builder.TicketBuilder;
import io.github.joaovictor.smartsupport.entity.Client;
import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;

public abstract class TicketFactory {

    public final Ticket createTicket(String title, String description, Client client, TicketPriority requestedPriority) {
        return TicketBuilder.newTicket()
                .title(title)
                .description(description)
                .client(client)
                .category(category())
                .priority(requestedPriority != null ? requestedPriority : defaultPriority())
                .build();
    }

    public abstract TicketCategory category();

    protected abstract TicketPriority defaultPriority();
}
