package io.github.joaovictor.smartsupport.strategy;

import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class UrgentPriorityStrategy extends KeywordPriorityStrategy {

    private static final List<String> KEYWORDS = List.of(
            "urgente", "critico", "crítico", "producao parada", "produção parada", "sistema fora do ar"
    );

    @Override
    public TicketPriority priority() {
        return TicketPriority.URGENT;
    }

    @Override
    protected List<String> keywords() {
        return KEYWORDS;
    }
}
