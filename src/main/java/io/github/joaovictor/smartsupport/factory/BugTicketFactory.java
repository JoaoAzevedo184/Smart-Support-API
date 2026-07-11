package io.github.joaovictor.smartsupport.factory;

import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;
import org.springframework.stereotype.Component;

/** Fábrica de chamados de bug (prioridade-default {@code HIGH}). */
@Component
public class BugTicketFactory extends TicketFactory {

    @Override
    public TicketCategory category() {
        return TicketCategory.BUG;
    }

    @Override
    protected TicketPriority defaultPriority() {
        return TicketPriority.HIGH;
    }
}
