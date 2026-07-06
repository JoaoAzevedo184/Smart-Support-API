package io.github.joaovictor.smartsupport.factory;

import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;
import org.springframework.stereotype.Component;

@Component
public class SupportTicketFactory extends TicketFactory {

    @Override
    public TicketCategory category() {
        return TicketCategory.SUPPORT;
    }

    @Override
    protected TicketPriority defaultPriority() {
        return TicketPriority.LOW;
    }
}
