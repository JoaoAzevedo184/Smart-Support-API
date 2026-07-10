package io.github.joaovictor.smartsupport.chain;

import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import io.github.joaovictor.smartsupport.repository.SupportTeamRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 5º e último elo do pipeline: atribui a equipe responsável conforme a categoria
 * do chamado (mapeamento categoria → nome da equipe). Se a equipe não existir
 * no banco, o chamado segue sem atribuição.
 */
@Component
@RequiredArgsConstructor
public class AssignTeamHandler extends TicketChainHandler {

    private static final Map<TicketCategory, String> TEAM_NAME_BY_CATEGORY = Map.of(
            TicketCategory.BUG, "Bug Team",
            TicketCategory.BILLING, "Billing Team",
            TicketCategory.SUPPORT, "Support Team"
    );

    private final SupportTeamRepository supportTeamRepository;

    @Override
    protected void process(TicketProcessingContext context) {
        Ticket ticket = context.getTicket();
        String teamName = TEAM_NAME_BY_CATEGORY.get(ticket.getCategory());
        if (teamName != null) {
            supportTeamRepository.findByName(teamName).ifPresent(ticket::setAssignedTeam);
        }
    }
}
