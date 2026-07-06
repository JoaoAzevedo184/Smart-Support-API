package io.github.joaovictor.smartsupport.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.joaovictor.smartsupport.entity.Client;
import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;
import io.github.joaovictor.smartsupport.entity.enums.TicketStatus;
import org.junit.jupiter.api.Test;

class TicketBuilderTest {

    private final Client client = Client.builder().name("Cliente").email("cliente@example.com").build();

    @Test
    void deveDefinirStatusOpenPorPadraoQuandoNaoInformado() {
        Ticket ticket = TicketBuilder.newTicket()
                .title("Título válido")
                .description("Descrição válida com detalhes")
                .client(client)
                .category(TicketCategory.BUG)
                .build();

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.OPEN);
    }

    @Test
    void devePreservarStatusExplicitoQuandoInformado() {
        Ticket ticket = TicketBuilder.newTicket()
                .title("Título válido")
                .description("Descrição válida com detalhes")
                .client(client)
                .category(TicketCategory.BUG)
                .priority(TicketPriority.HIGH)
                .status(TicketStatus.IN_PROGRESS)
                .build();

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
        assertThat(ticket.getPriority()).isEqualTo(TicketPriority.HIGH);
    }

    @Test
    void deveLancarExcecaoQuandoCampoObrigatorioFaltando() {
        assertThatThrownBy(() -> TicketBuilder.newTicket()
                .description("Descrição válida com detalhes")
                .client(client)
                .category(TicketCategory.BUG)
                .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("title");
    }
}
