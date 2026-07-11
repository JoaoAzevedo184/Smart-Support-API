package io.github.joaovictor.smartsupport.classifier;

import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 * Baseline determinística, sem dependência externa: classifica por palavras-chave.
 * Usada como classificador padrão e como fallback de {@link AiTicketClassifier}.
 */
@Component
public class RuleBasedClassifier implements ModeAwareTicketClassifier {

    private static final List<String> BUG_KEYWORDS = List.of(
            "erro", "bug", "falha", "travou", "travando", "quebrado", "não funciona", "nao funciona", "exception"
    );
    private static final List<String> BILLING_KEYWORDS = List.of(
            "cobrança", "cobranca", "fatura", "boleto", "pagamento", "reembolso", "cartão", "cartao", "cobrado"
    );

    @Override
    public ClassifierMode mode() {
        return ClassifierMode.RULES;
    }

    @Override
    public TicketCategory classify(String title, String description) {
        String content = (title + " " + description).toLowerCase(Locale.ROOT);

        if (BUG_KEYWORDS.stream().anyMatch(content::contains)) {
            return TicketCategory.BUG;
        }
        if (BILLING_KEYWORDS.stream().anyMatch(content::contains)) {
            return TicketCategory.BILLING;
        }
        return TicketCategory.SUPPORT;
    }
}
