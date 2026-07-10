package io.github.joaovictor.smartsupport.builder;

import io.github.joaovictor.smartsupport.entity.Client;
import io.github.joaovictor.smartsupport.entity.SupportTeam;
import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.User;
import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;
import io.github.joaovictor.smartsupport.entity.enums.TicketStatus;
import java.util.Objects;

/**
 * Builder da entidade {@link Ticket}: construção fluente para uma entidade com
 * muitos atributos, evitando construtor telescópico. Valida os campos
 * obrigatórios no {@link #build()} e aplica {@code OPEN} como status default.
 */
public class TicketBuilder {

    private final Ticket ticket = new Ticket();

    // ===== Criação =====
    public static TicketBuilder newTicket() {
        return new TicketBuilder();
    }

    // ===== Passos fluentes =====
    public TicketBuilder title(String title) {
        ticket.setTitle(title);
        return this;
    }

    public TicketBuilder description(String description) {
        ticket.setDescription(description);
        return this;
    }

    public TicketBuilder client(Client client) {
        ticket.setClient(client);
        return this;
    }

    public TicketBuilder category(TicketCategory category) {
        ticket.setCategory(category);
        return this;
    }

    public TicketBuilder priority(TicketPriority priority) {
        ticket.setPriority(priority);
        return this;
    }

    public TicketBuilder status(TicketStatus status) {
        ticket.setStatus(status);
        return this;
    }

    public TicketBuilder assignedTeam(SupportTeam supportTeam) {
        ticket.setAssignedTeam(supportTeam);
        return this;
    }

    public TicketBuilder assignedUser(User user) {
        ticket.setAssignedUser(user);
        return this;
    }

    // ===== Finalização (valida obrigatórios e aplica defaults) =====
    public Ticket build() {
        Objects.requireNonNull(ticket.getTitle(), "title é obrigatório");
        Objects.requireNonNull(ticket.getDescription(), "description é obrigatória");
        Objects.requireNonNull(ticket.getClient(), "client é obrigatório");
        Objects.requireNonNull(ticket.getCategory(), "category é obrigatória");

        if (ticket.getStatus() == null) {
            ticket.setStatus(TicketStatus.OPEN);
        }
        return ticket;
    }
}
