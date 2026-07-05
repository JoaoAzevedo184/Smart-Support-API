package io.github.joaovictor.smartsupport.strategy;

import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PriorityResolver {

    private final List<PriorityStrategy> strategiesBySeverityDesc;

    public PriorityResolver(List<PriorityStrategy> strategies) {
        this.strategiesBySeverityDesc = strategies.stream()
                .sorted(Comparator.comparing((PriorityStrategy s) -> s.priority().ordinal()).reversed())
                .toList();
    }

    public void resolve(Ticket ticket) {
        TicketPriority current = ticket.getPriority();
        for (PriorityStrategy strategy : strategiesBySeverityDesc) {
            TicketPriority candidate = strategy.priority();
            if (candidate.ordinal() <= current.ordinal()) {
                break;
            }
            if (strategy.matches(ticket)) {
                ticket.setPriority(candidate);
                break;
            }
        }
    }
}
