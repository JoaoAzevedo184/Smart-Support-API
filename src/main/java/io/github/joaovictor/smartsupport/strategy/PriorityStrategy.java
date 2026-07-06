package io.github.joaovictor.smartsupport.strategy;

import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;

public interface PriorityStrategy {

    TicketPriority priority();

    boolean matches(Ticket ticket);
}
