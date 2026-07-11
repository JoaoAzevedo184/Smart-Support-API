package io.github.joaovictor.smartsupport.chain;

import io.github.joaovictor.smartsupport.classifier.TicketClassifier;
import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.template.CategoryProcessorProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 3º elo do pipeline: define a categoria e aplica o processamento por categoria
 * (Template Method). Se o cliente não informou a categoria, delega a inferência
 * ao {@link TicketClassifier} (regras ou IA — Strategy).
 */
@Component
@RequiredArgsConstructor
public class CategoryHandler extends TicketChainHandler {

    // ===== Colaboradores =====
    private final CategoryProcessorProvider categoryProcessorProvider;
    private final TicketClassifier ticketClassifier;

    @Override
    protected void process(TicketProcessingContext context) {
        Ticket ticket = context.getTicket();
        if (!context.hasExplicitCategory()) {
            ticket.setCategory(ticketClassifier.classify(ticket.getTitle(), ticket.getDescription()));
        }
        categoryProcessorProvider.getProcessor(ticket.getCategory()).process(ticket);
    }
}
