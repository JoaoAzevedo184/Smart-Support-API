package io.github.joaovictor.smartsupport.chain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.joaovictor.smartsupport.dto.ticket.TicketRequest;
import io.github.joaovictor.smartsupport.entity.Client;
import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class ValidationHandlerTest {

    private final ValidationHandler handler = new ValidationHandler();

    private TicketProcessingContext contextFor(String title, String description) {
        Ticket ticket = Ticket.builder()
                .title(title)
                .description(description)
                .client(Client.builder().build())
                .build();
        TicketRequest request = new TicketRequest(title, description, null, TicketCategory.BUG, null);
        return new TicketProcessingContext(ticket, request);
    }

    @Test
    void deveAceitarTituloEDescricaoValidos() {
        assertThatCode(() -> handler.handle(contextFor("Título válido", "Descrição com detalhes suficientes")))
                .doesNotThrowAnyException();
    }

    @Test
    void deveRejeitarTituloCurto() {
        assertThatThrownBy(() -> handler.handle(contextFor("Oi", "Descrição com detalhes suficientes")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("título");
    }

    @Test
    void deveRejeitarDescricaoCurta() {
        assertThatThrownBy(() -> handler.handle(contextFor("Título válido", "curta")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("descrição");
    }
}
