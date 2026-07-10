package io.github.joaovictor.smartsupport.facade;

import io.github.joaovictor.smartsupport.chain.TicketProcessingChain;
import io.github.joaovictor.smartsupport.chain.TicketProcessingContext;
import io.github.joaovictor.smartsupport.dto.ticket.TicketAssignRequest;
import io.github.joaovictor.smartsupport.dto.ticket.TicketReportResponse;
import io.github.joaovictor.smartsupport.dto.ticket.TicketRequest;
import io.github.joaovictor.smartsupport.dto.ticket.TicketResponse;
import io.github.joaovictor.smartsupport.dto.ticket.TicketStatusUpdateRequest;
import io.github.joaovictor.smartsupport.entity.Client;
import io.github.joaovictor.smartsupport.entity.SupportTeam;
import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.User;
import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;
import io.github.joaovictor.smartsupport.entity.enums.TicketStatus;
import io.github.joaovictor.smartsupport.event.TicketAssignedEvent;
import io.github.joaovictor.smartsupport.event.TicketStatusChangedEvent;
import io.github.joaovictor.smartsupport.factory.TicketFactory;
import io.github.joaovictor.smartsupport.factory.TicketFactoryProvider;
import io.github.joaovictor.smartsupport.mapper.TicketMapper;
import io.github.joaovictor.smartsupport.repository.ClientRepository;
import io.github.joaovictor.smartsupport.repository.SupportTeamRepository;
import io.github.joaovictor.smartsupport.repository.TicketRepository;
import io.github.joaovictor.smartsupport.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class TicketFacade {

    private final ClientRepository clientRepository;
    private final TicketRepository ticketRepository;
    private final SupportTeamRepository supportTeamRepository;
    private final UserRepository userRepository;
    private final TicketFactoryProvider ticketFactoryProvider;
    private final TicketMapper ticketMapper;
    private final TicketProcessingChain ticketProcessingChain;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public TicketResponse openTicket(TicketRequest request) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        // categoria explícita ou SUPPORT como base neutra até o CategoryHandler classificar o texto
        TicketCategory initialCategory = request.category() != null ? request.category() : TicketCategory.SUPPORT;
        TicketFactory factory = ticketFactoryProvider.getFactory(initialCategory);

        Ticket ticket = factory.createTicket(
                request.title(),
                request.description(),
                client,
                request.priority()
        );

        ticketProcessingChain.process(new TicketProcessingContext(ticket, request));

        return ticketMapper.toResponse(ticketRepository.save(ticket));
    }

    @Transactional
    public TicketResponse changeStatus(UUID id, TicketStatusUpdateRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chamado não encontrado"));

        TicketStatus previousStatus = ticket.getStatus();
        TicketStatus newStatus = request.status();

        if (!previousStatus.canTransitionTo(newStatus)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Transição de status inválida: " + previousStatus + " -> " + newStatus);
        }

        ticket.setStatus(newStatus);
        if (newStatus == TicketStatus.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
        }

        Ticket saved = ticketRepository.save(ticket);

        eventPublisher.publishEvent(new TicketStatusChangedEvent(saved, previousStatus, newStatus));

        return ticketMapper.toResponse(saved);
    }

    @Transactional
    public TicketResponse assign(UUID id, TicketAssignRequest request) {
        if (request.teamId() == null && request.userId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Informe ao menos um time ou usuário para atribuição");
        }

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chamado não encontrado"));

        SupportTeam team = null;
        if (request.teamId() != null) {
            team = supportTeamRepository.findById(request.teamId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Time de suporte não encontrado"));
            ticket.setAssignedTeam(team);
        }

        User user = null;
        if (request.userId() != null) {
            user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
            ticket.setAssignedUser(user);
        }

        Ticket saved = ticketRepository.save(ticket);

        eventPublisher.publishEvent(new TicketAssignedEvent(saved, team, user));

        return ticketMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> listOpenTickets() {
        return ticketRepository.findByStatus(TicketStatus.OPEN).stream()
                .map(ticketMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TicketReportResponse generateReport() {
        Map<TicketStatus, Long> byStatus = new EnumMap<>(TicketStatus.class);
        for (TicketStatus status : TicketStatus.values()) {
            byStatus.put(status, ticketRepository.countByStatus(status));
        }

        Map<TicketCategory, Long> byCategory = new EnumMap<>(TicketCategory.class);
        for (TicketCategory category : TicketCategory.values()) {
            byCategory.put(category, ticketRepository.countByCategory(category));
        }

        Map<TicketPriority, Long> byPriority = new EnumMap<>(TicketPriority.class);
        for (TicketPriority priority : TicketPriority.values()) {
            byPriority.put(priority, ticketRepository.countByPriority(priority));
        }

        return new TicketReportResponse(ticketRepository.count(), byStatus, byCategory, byPriority);
    }
}
