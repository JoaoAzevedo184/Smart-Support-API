package io.github.joaovictor.smartsupport.repository;

import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;
import io.github.joaovictor.smartsupport.entity.enums.TicketStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByClientId(UUID clientId);

    List<Ticket> findByAssignedTeamId(UUID supportTeamId);

    List<Ticket> findByAssignedUserId(UUID userId);

    List<Ticket> findByStatusAndPriority(TicketStatus status, TicketPriority priority);

    long countByStatus(TicketStatus status);

    long countByCategory(TicketCategory category);

    long countByPriority(TicketPriority priority);
}
