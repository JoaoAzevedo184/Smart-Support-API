package io.github.joaovictor.smartsupport.chain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.github.joaovictor.smartsupport.classifier.RuleBasedClassifier;
import io.github.joaovictor.smartsupport.dto.ticket.TicketRequest;
import io.github.joaovictor.smartsupport.entity.Client;
import io.github.joaovictor.smartsupport.entity.SupportTeam;
import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;
import io.github.joaovictor.smartsupport.repository.SupportTeamRepository;
import io.github.joaovictor.smartsupport.strategy.PriorityResolver;
import io.github.joaovictor.smartsupport.strategy.UrgentPriorityStrategy;
import io.github.joaovictor.smartsupport.template.BugTicketProcessor;
import io.github.joaovictor.smartsupport.template.CategoryProcessorProvider;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class TicketProcessingChainTest {

    @Mock
    private SupportTeamRepository supportTeamRepository;

    private TicketProcessingChain chain;

    @BeforeEach
    void setUp() {
        PriorityResolver priorityResolver = new PriorityResolver(List.of(new UrgentPriorityStrategy()));
        CategoryProcessorProvider categoryProcessorProvider =
                new CategoryProcessorProvider(List.of(new BugTicketProcessor()));

        chain = new TicketProcessingChain(
                new ValidationHandler(),
                new SpamCheckHandler(),
                new CategoryHandler(categoryProcessorProvider, new RuleBasedClassifier()),
                new PriorityHandler(priorityResolver),
                new AssignTeamHandler(supportTeamRepository));
    }

    @Test
    void deveProcessarTicketValidoEEscalonarPrioridadePorPalavraChave() {
        SupportTeam bugTeam = SupportTeam.builder().name("Bug Team").build();
        when(supportTeamRepository.findByName("Bug Team")).thenReturn(Optional.of(bugTeam));

        Ticket ticket = Ticket.builder()
                .title("Sistema fora do ar")
                .description("Produção parada, ninguém consegue acessar o sistema")
                .client(Client.builder().build())
                .category(TicketCategory.BUG)
                .priority(TicketPriority.LOW)
                .build();
        TicketRequest request = new TicketRequest(ticket.getTitle(), ticket.getDescription(), null, TicketCategory.BUG, null);

        chain.process(new TicketProcessingContext(ticket, request));

        assertThat(ticket.getPriority()).isEqualTo(TicketPriority.URGENT);
        assertThat(ticket.getAssignedTeam()).isEqualTo(bugTeam);
    }

    @Test
    void deveClassificarCategoriaAutomaticamenteQuandoNaoInformada() {
        SupportTeam bugTeam = SupportTeam.builder().name("Bug Team").build();
        when(supportTeamRepository.findByName("Bug Team")).thenReturn(Optional.of(bugTeam));

        Ticket ticket = Ticket.builder()
                .title("Sistema apresentando erro grave")
                .description("Ao tentar salvar o formulário, o sistema retorna uma exception")
                .client(Client.builder().build())
                .category(TicketCategory.SUPPORT) // placeholder até a classificação (ver TicketFacade)
                .priority(TicketPriority.LOW)
                .build();
        TicketRequest request = new TicketRequest(ticket.getTitle(), ticket.getDescription(), null, null, null);

        chain.process(new TicketProcessingContext(ticket, request));

        assertThat(ticket.getCategory()).isEqualTo(TicketCategory.BUG);
        assertThat(ticket.getAssignedTeam()).isEqualTo(bugTeam);
    }

    @Test
    void deveInterromperCadeiaQuandoValidacaoFalhar() {
        Ticket ticket = Ticket.builder()
                .title("Oi")
                .description("Descrição com detalhes suficientes")
                .client(Client.builder().build())
                .category(TicketCategory.BUG)
                .priority(TicketPriority.LOW)
                .build();
        TicketRequest request = new TicketRequest(ticket.getTitle(), ticket.getDescription(), null, TicketCategory.BUG, null);

        assertThatThrownBy(() -> chain.process(new TicketProcessingContext(ticket, request)))
                .isInstanceOf(ResponseStatusException.class);
    }
}
