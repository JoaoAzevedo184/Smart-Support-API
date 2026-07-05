package io.github.joaovictor.smartsupport.strategy;

import io.github.joaovictor.smartsupport.entity.Ticket;
import java.util.List;
import java.util.Locale;

abstract class KeywordPriorityStrategy implements PriorityStrategy {

    protected abstract List<String> keywords();

    @Override
    public boolean matches(Ticket ticket) {
        String content = (ticket.getTitle() + " " + ticket.getDescription()).toLowerCase(Locale.ROOT);
        return keywords().stream().anyMatch(content::contains);
    }
}
