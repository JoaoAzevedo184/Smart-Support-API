package io.github.joaovictor.smartsupport.classifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;

class TicketClassifierResolverTest {

    private final RuleBasedClassifier rulesClassifier = new RuleBasedClassifier();
    private final ModeAwareTicketClassifier aiClassifier = mock(ModeAwareTicketClassifier.class);

    private final List<ModeAwareTicketClassifier> classifiers;

    TicketClassifierResolverTest() {
        when(aiClassifier.mode()).thenReturn(ClassifierMode.AI);
        this.classifiers = List.of(rulesClassifier, aiClassifier);
    }

    @Test
    void deveSelecionarModoAiQuandoConfigurado() {
        TicketClassifierResolver resolver = new TicketClassifierResolver(classifiers, "ai");

        assertThat(resolver.getActive()).isSameAs(aiClassifier);
    }

    @Test
    void deveSelecionarModoRulesQuandoConfigurado() {
        TicketClassifierResolver resolver = new TicketClassifierResolver(classifiers, "rules");

        assertThat(resolver.getActive()).isSameAs(rulesClassifier);
    }

    @Test
    void deveCairParaRulesQuandoModoDesconhecido() {
        TicketClassifierResolver resolver = new TicketClassifierResolver(classifiers, "modo-invalido");

        assertThat(resolver.getActive()).isSameAs(rulesClassifier);
    }
}
