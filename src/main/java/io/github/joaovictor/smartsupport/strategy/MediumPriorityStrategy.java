package io.github.joaovictor.smartsupport.strategy;

import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;
import java.util.List;
import org.springframework.stereotype.Component;

/** Estratégia MEDIUM: palavras-chave de dúvida / problema recorrente. */
@Component
public class MediumPriorityStrategy extends KeywordPriorityStrategy {

    private static final List<String> KEYWORDS = List.of(
            "duvida", "dúvida", "problema recorrente"
    );

    @Override
    public TicketPriority priority() {
        return TicketPriority.MEDIUM;
    }

    @Override
    protected List<String> keywords() {
        return KEYWORDS;
    }
}
