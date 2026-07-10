package io.github.joaovictor.smartsupport.factory;

import io.github.joaovictor.smartsupport.builder.TicketBuilder;
import io.github.joaovictor.smartsupport.entity.Client;
import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;

/**
 * Factory Method: cada subclasse cria um chamado da sua categoria com os
 * defaults apropriados. O template {@link #createTicket} fixa a montagem (via
 * Builder) e delega às subclasses a {@link #category} e a {@link #defaultPriority}.
 */
public abstract class TicketFactory {

    /** Template de criação: usa a categoria/prioridade-default da subclasse. */
    public final Ticket createTicket(String title, String description, Client client, TicketPriority requestedPriority) {
        return TicketBuilder.newTicket()
                .title(title)
                .description(description)
                .client(client)
                .category(category())
                .priority(requestedPriority != null ? requestedPriority : defaultPriority())
                .build();
    }

    // ===== Pontos de variação (definidos por cada fábrica concreta) =====
    public abstract TicketCategory category();

    protected abstract TicketPriority defaultPriority();
}
