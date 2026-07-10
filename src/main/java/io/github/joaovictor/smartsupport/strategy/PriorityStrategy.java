package io.github.joaovictor.smartsupport.strategy;

import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;

/**
 * Strategy de prioridade: cada implementação representa um nível e sabe dizer
 * se um chamado {@link #matches} aquele nível. O {@link PriorityResolver}
 * combina as estratégias para escolher a prioridade final.
 */
public interface PriorityStrategy {

    /** Nível que esta estratégia representa. */
    TicketPriority priority();

    /** Verdadeiro se o chamado se enquadra neste nível. */
    boolean matches(Ticket ticket);
}
