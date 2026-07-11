package io.github.joaovictor.smartsupport.template;

import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;

/**
 * Template Method do processamento por categoria: {@link #process} fixa o
 * esqueleto (validar → enriquecer) e cada categoria sobrescreve os passos que
 * variam. Os passos têm implementação vazia por padrão (hooks opcionais).
 */
public abstract class CategoryProcessor {

    /** Template method: esqueleto fixo do processamento. */
    public final void process(Ticket ticket) {
        validate(ticket);
        enrich(ticket);
    }

    // ===== Identidade =====
    public abstract TicketCategory category();

    // ===== Passos sobrescrevíveis (hooks) =====
    protected void validate(Ticket ticket) {
    }

    protected void enrich(Ticket ticket) {
    }
}
