package io.github.joaovictor.smartsupport.command;

import io.github.joaovictor.smartsupport.dto.ticket.TicketAssignRequest;
import io.github.joaovictor.smartsupport.dto.ticket.TicketResponse;
import io.github.joaovictor.smartsupport.facade.TicketFacade;
import java.util.UUID;

/**
 * Command concreto: atribui equipe e/ou usuário ao chamado,
 * delegando a operação à {@link TicketFacade}.
 */
public class AssignTicketCommand implements TicketCommand {

    // ===== Alvo da ação =====
    private final TicketFacade ticketFacade;
    private final UUID ticketId;
    private final TicketAssignRequest request;

    public AssignTicketCommand(TicketFacade ticketFacade, UUID ticketId, TicketAssignRequest request) {
        this.ticketFacade = ticketFacade;
        this.ticketId = ticketId;
        this.request = request;
    }

    @Override
    public TicketResponse execute() {
        return ticketFacade.assign(ticketId, request);
    }
}
