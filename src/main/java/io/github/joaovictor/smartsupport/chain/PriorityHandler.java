package io.github.joaovictor.smartsupport.chain;

import io.github.joaovictor.smartsupport.strategy.PriorityResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 4º elo do pipeline: resolve a prioridade via {@link PriorityResolver} (Strategy),
 * mas só quando ela não foi informada explicitamente no request.
 */
@Component
@RequiredArgsConstructor
public class PriorityHandler extends TicketChainHandler {

    private final PriorityResolver priorityResolver;

    @Override
    protected void process(TicketProcessingContext context) {
        if (context.hasExplicitPriority()) {
            return;
        }
        priorityResolver.resolve(context.getTicket());
    }
}
