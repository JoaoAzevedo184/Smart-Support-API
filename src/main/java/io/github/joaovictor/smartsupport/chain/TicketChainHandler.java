package io.github.joaovictor.smartsupport.chain;

/**
 * Elo base da Chain of Responsibility do pipeline de chamados.
 * Cada handler concreto implementa {@link #process} e, ao final, o elo repassa
 * o contexto ao próximo automaticamente ({@link #handle}). Interromper a cadeia
 * é lançar uma exceção em {@code process} (ex.: validação/spam falham).
 */
public abstract class TicketChainHandler {

    private TicketChainHandler next;

    // ===== Montagem da cadeia =====
    public TicketChainHandler linkWith(TicketChainHandler next) {
        this.next = next;
        return next;
    }

    // ===== Execução (template do elo: processa e repassa) =====
    public final void handle(TicketProcessingContext context) {
        process(context);
        if (next != null) {
            next.handle(context);
        }
    }

    /** Etapa concreta do elo. Lançar exceção aqui interrompe o pipeline. */
    protected abstract void process(TicketProcessingContext context);
}
