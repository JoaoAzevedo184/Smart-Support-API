package io.github.joaovictor.smartsupport.chain;

import io.github.joaovictor.smartsupport.classifier.TicketClassifier;
import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.template.CategoryProcessorProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryHandler extends TicketChainHandler {

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
