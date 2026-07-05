package io.github.joaovictor.smartsupport.strategy;

import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;
import org.springframework.stereotype.Component;

@Component
public class LowPriorityStrategy implements PriorityStrategy {

    @Override
    public TicketPriority priority() {
        return TicketPriority.LOW;
    }

    @Override
    public boolean matches(Ticket ticket) {
        return true;
    }
}
