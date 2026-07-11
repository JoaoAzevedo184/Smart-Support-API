package io.github.joaovictor.smartsupport.strategy;

import io.github.joaovictor.smartsupport.entity.Ticket;
import java.util.List;
import java.util.Locale;

/**
 * Base para estratégias que decidem o nível por presença de palavras-chave no
 * título/descrição. As subclasses só declaram suas {@link #keywords()}.
 */
abstract class KeywordPriorityStrategy implements PriorityStrategy {

    protected abstract List<String> keywords();

    @Override
    public boolean matches(Ticket ticket) {
        String content = (ticket.getTitle() + " " + ticket.getDescription()).toLowerCase(Locale.ROOT);
        return keywords().stream().anyMatch(content::contains);
    }
}
