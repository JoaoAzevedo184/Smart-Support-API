package io.github.joaovictor.smartsupport.classifier;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import org.junit.jupiter.api.Test;

class RuleBasedClassifierTest {

    private final RuleBasedClassifier classifier = new RuleBasedClassifier();

    @Test
    void deveClassificarComoBugQuandoContemPalavraChaveDeErro() {
        TicketCategory category = classifier.classify("Sistema com erro", "O sistema apresenta uma exception ao salvar");

        assertThat(category).isEqualTo(TicketCategory.BUG);
    }

    @Test
    void deveClassificarComoBillingQuandoContemPalavraChaveDeCobranca() {
        TicketCategory category = classifier.classify("Dúvida sobre cobrança", "Fui cobrado duas vezes no cartão");

        assertThat(category).isEqualTo(TicketCategory.BILLING);
    }

    @Test
    void deveClassificarComoSupportQuandoNaoReconhecePalavraChave() {
        TicketCategory category = classifier.classify("Dúvida geral", "Como faço para trocar minha senha?");

        assertThat(category).isEqualTo(TicketCategory.SUPPORT);
    }

    @Test
    void deveExporModoRules() {
        assertThat(classifier.mode()).isEqualTo(ClassifierMode.RULES);
    }
}
