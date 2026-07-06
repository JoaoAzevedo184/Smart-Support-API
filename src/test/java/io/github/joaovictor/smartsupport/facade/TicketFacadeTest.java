package io.github.joaovictor.smartsupport.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.joaovictor.smartsupport.chain.TicketProcessingChain;
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
import io.github.joaovictor.smartsupport.entity.enums.TicketStatus;
import io.github.joaovictor.smartsupport.factory.BugTicketFactory;
import io.github.joaovictor.smartsupport.factory.TicketFactoryProvider;
import io.github.joaovictor.smartsupport.mapper.TicketMapper;
import io.github.joaovictor.smartsupport.repository.ClientRepository;
import io.github.joaovictor.smartsupport.repository.SupportTeamRepository;
import io.github.joaovictor.smartsupport.repository.TicketRepository;
import io.github.joaovictor.smartsupport.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class TicketFacadeTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private SupportTeamRepository supportTeamRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TicketFactoryProvider ticketFactoryProvider;
    @Mock
    private TicketMapper ticketMapper;
    @Mock
    private TicketProcessingChain ticketProcessingChain;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TicketFacade ticketFacade;

    private final TicketResponse stubResponse = new TicketResponse(
            UUID.randomUUID(), "t", "d", TicketStatus.OPEN, null, TicketCategory.BUG,
            UUID.randomUUID(), "cliente", null, null, null, null, null, null, null);

    @Test
    void deveAbrirTicketQuandoClienteExiste() {
        Client client = Client.builder().id(UUID.randomUUID()).name("Cliente").build();
        TicketRequest request = new TicketRequest("Título válido", "Descrição válida com detalhes", client.getId(),
                TicketCategory.BUG, null);

        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(ticketFactoryProvider.getFactory(TicketCategory.BUG)).thenReturn(new BugTicketFactory());
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ticketMapper.toResponse(any(Ticket.class))).thenReturn(stubResponse);

        TicketResponse response = ticketFacade.openTicket(request);

        assertThat(response).isEqualTo(stubResponse);
        verify(ticketProcessingChain).process(any());
    }

    @Test
    void deveLancar404QuandoClienteNaoExisteAoAbrirTicket() {
        UUID clientId = UUID.randomUUID();
        TicketRequest request = new TicketRequest("Título válido", "Descrição válida com detalhes", clientId,
                TicketCategory.BUG, null);
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketFacade.openTicket(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Cliente não encontrado");
    }

    @Test
    void deveMudarStatusQuandoTransicaoValida() {
        UUID ticketId = UUID.randomUUID();
        Ticket ticket = Ticket.builder().id(ticketId).status(TicketStatus.OPEN).client(Client.builder().build()).build();
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        when(ticketMapper.toResponse(ticket)).thenReturn(stubResponse);

        TicketResponse response = ticketFacade.changeStatus(ticketId, new TicketStatusUpdateRequest(TicketStatus.IN_PROGRESS));

        assertThat(response).isEqualTo(stubResponse);
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
        assertThat(ticket.getClosedAt()).isNull();
    }

    @Test
    void deveDefinirClosedAtAoFecharTicket() {
        UUID ticketId = UUID.randomUUID();
        Ticket ticket = Ticket.builder().id(ticketId).status(TicketStatus.OPEN).client(Client.builder().build()).build();
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        when(ticketMapper.toResponse(ticket)).thenReturn(stubResponse);

        ticketFacade.changeStatus(ticketId, new TicketStatusUpdateRequest(TicketStatus.CLOSED));

        assertThat(ticket.getClosedAt()).isNotNull();
    }

    @Test
    void deveLancar409QuandoTransicaoDeStatusInvalida() {
        UUID ticketId = UUID.randomUUID();
        Ticket ticket = Ticket.builder().id(ticketId).status(TicketStatus.OPEN).client(Client.builder().build()).build();
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> ticketFacade.changeStatus(ticketId, new TicketStatusUpdateRequest(TicketStatus.RESOLVED)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("inválida");
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void deveAtribuirTimeEUsuario() {
        UUID ticketId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Ticket ticket = Ticket.builder().id(ticketId).status(TicketStatus.OPEN).client(Client.builder().build()).build();
        SupportTeam team = SupportTeam.builder().id(teamId).name("Bug Team").build();
        User user = User.builder().id(userId).name("Agente").build();

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(supportTeamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        when(ticketMapper.toResponse(ticket)).thenReturn(stubResponse);

        ticketFacade.assign(ticketId, new TicketAssignRequest(teamId, userId));

        assertThat(ticket.getAssignedTeam()).isEqualTo(team);
        assertThat(ticket.getAssignedUser()).isEqualTo(user);
    }

    @Test
    void deveLancar400QuandoAssignSemTimeOuUsuario() {
        UUID ticketId = UUID.randomUUID();

        assertThatThrownBy(() -> ticketFacade.assign(ticketId, new TicketAssignRequest(null, null)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("ao menos um");
    }

    @Test
    void deveListarTicketsAbertos() {
        Ticket openTicket = Ticket.builder().status(TicketStatus.OPEN).client(Client.builder().build()).build();
        when(ticketRepository.findByStatus(TicketStatus.OPEN)).thenReturn(List.of(openTicket));
        when(ticketMapper.toResponse(openTicket)).thenReturn(stubResponse);

        List<TicketResponse> result = ticketFacade.listOpenTickets();

        assertThat(result).containsExactly(stubResponse);
    }

    @Test
    void deveGerarRelatorioComTotalDeTickets() {
        when(ticketRepository.count()).thenReturn(5L);

        TicketReportResponse report = ticketFacade.generateReport();

        assertThat(report.totalTickets()).isEqualTo(5L);
        assertThat(report.byStatus()).containsKey(TicketStatus.OPEN);
        assertThat(report.byCategory()).containsKey(TicketCategory.BUG);
    }
}
