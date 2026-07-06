package io.github.joaovictor.smartsupport.strategy;

import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class HighPriorityStrategy extends KeywordPriorityStrategy {

    private static final List<String> KEYWORDS = List.of(
            "importante", "bloqueado", "bloqueando", "erro grave"
    );

    @Override
    public TicketPriority priority() {
        return TicketPriority.HIGH;
    }

    @Override
    protected List<String> keywords() {
        return KEYWORDS;
    }
}
