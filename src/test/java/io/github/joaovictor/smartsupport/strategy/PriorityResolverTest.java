package io.github.joaovictor.smartsupport.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.joaovictor.smartsupport.entity.Client;
import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;
import java.util.List;
import org.junit.jupiter.api.Test;

class PriorityResolverTest {

    private final PriorityResolver resolver = new PriorityResolver(List.of(
            new UrgentPriorityStrategy(), new HighPriorityStrategy(), new MediumPriorityStrategy(), new LowPriorityStrategy()));

    private Ticket ticketWith(String title, String description, TicketPriority current) {
        return Ticket.builder()
                .title(title)
                .description(description)
                .client(Client.builder().build())
                .priority(current)
                .build();
    }

    @Test
    void deveEscalonarParaUrgenteQuandoContemPalavraChaveCritica() {
        Ticket ticket = ticketWith("Sistema fora do ar", "Produção parada, ninguém acessa", TicketPriority.LOW);

        resolver.resolve(ticket);

        assertThat(ticket.getPriority()).isEqualTo(TicketPriority.URGENT);
    }

    @Test
    void naoDeveRebaixarPrioridadeJaSuperiorAMatch() {
        Ticket ticket = ticketWith("Solicitação simples", "Apenas uma dúvida sobre a fatura", TicketPriority.URGENT);

        resolver.resolve(ticket);

        assertThat(ticket.getPriority()).isEqualTo(TicketPriority.URGENT);
    }
}
