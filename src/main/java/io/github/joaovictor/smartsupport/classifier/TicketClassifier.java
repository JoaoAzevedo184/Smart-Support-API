package io.github.joaovictor.smartsupport.classifier;

import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;

/**
 * Contrato de classificação de categoria a partir do texto do chamado.
 * Ponto de extensão do pipeline: injetado no {@code CategoryHandler}, tem
 * implementações por regras e por IA, intercambiáveis via Strategy.
 */
public interface TicketClassifier {

    TicketCategory classify(String title, String description);
}
