package io.github.joaovictor.smartsupport.facade;

import io.github.joaovictor.smartsupport.chain.TicketProcessingChain;
import io.github.joaovictor.smartsupport.chain.TicketProcessingContext;
import io.github.joaovictor.smartsupport.dto.ticket.TicketRequest;
import io.github.joaovictor.smartsupport.dto.ticket.TicketResponse;
import io.github.joaovictor.smartsupport.entity.Client;
import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.factory.TicketFactory;
import io.github.joaovictor.smartsupport.factory.TicketFactoryProvider;
import io.github.joaovictor.smartsupport.mapper.TicketMapper;
import io.github.joaovictor.smartsupport.repository.ClientRepository;
import io.github.joaovictor.smartsupport.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class TicketFacade {

    private final ClientRepository clientRepository;
    private final TicketRepository ticketRepository;
    private final TicketFactoryProvider ticketFactoryProvider;
    private final TicketMapper ticketMapper;
    private final TicketProcessingChain ticketProcessingChain;

    @Transactional
    public TicketResponse openTicket(TicketRequest request) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        TicketFactory factory = ticketFactoryProvider.getFactory(request.category());

        Ticket ticket = factory.createTicket(
                request.title(),
                request.description(),
                client,
                request.priority()
        );

        ticketProcessingChain.process(new TicketProcessingContext(ticket, request));

        return ticketMapper.toResponse(ticketRepository.save(ticket));
    }
}
