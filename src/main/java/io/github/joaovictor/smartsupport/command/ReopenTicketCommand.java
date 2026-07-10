package io.github.joaovictor.smartsupport.command;

import io.github.joaovictor.smartsupport.dto.ticket.TicketResponse;
import io.github.joaovictor.smartsupport.dto.ticket.TicketStatusUpdateRequest;
import io.github.joaovictor.smartsupport.entity.enums.TicketStatus;
import io.github.joaovictor.smartsupport.facade.TicketFacade;
import java.util.UUID;

/**
 * Command concreto: reabre o chamado, delegando a transição de status
 * ({@code -> REOPENED}) à {@link TicketFacade}.
 */
public class ReopenTicketCommand implements TicketCommand {

    // ===== Alvo da ação =====
    private final TicketFacade ticketFacade;
    private final UUID ticketId;

    public ReopenTicketCommand(TicketFacade ticketFacade, UUID ticketId) {
        this.ticketFacade = ticketFacade;
        this.ticketId = ticketId;
    }

    @Override
    public TicketResponse execute() {
        return ticketFacade.changeStatus(ticketId, new TicketStatusUpdateRequest(TicketStatus.REOPENED));
    }
}
