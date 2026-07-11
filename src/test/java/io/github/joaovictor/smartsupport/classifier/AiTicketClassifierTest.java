package io.github.joaovictor.smartsupport.classifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

/**
 * Nenhum teste aqui chama um LLM real: {@link ChatClient} é totalmente mockado,
 * validando apenas o parsing da resposta e o fallback por regras.
 */
class AiTicketClassifierTest {

    private ChatClient chatClient;
    private ChatClient.ChatClientRequestSpec requestSpec;
    private ChatClient.CallResponseSpec callResponseSpec;
    private AiTicketClassifier classifier;

    @BeforeEach
    void setUp() {
        ChatClient.Builder builder = mock(ChatClient.Builder.class);
        chatClient = mock(ChatClient.class);
        requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        callResponseSpec = mock(ChatClient.CallResponseSpec.class);

        when(builder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);

        classifier = new AiTicketClassifier(builder, new RuleBasedClassifier());
    }

    @Test
    void deveClassificarConformeRespostaDaIa() {
        when(callResponseSpec.content()).thenReturn("BILLING");

        TicketCategory category = classifier.classify("Cobrança duplicada", "Fui cobrado duas vezes este mês");

        assertThat(category).isEqualTo(TicketCategory.BILLING);
    }

    @Test
    void deveTolerarRespostaComTextoAlemDaCategoria() {
        when(callResponseSpec.content()).thenReturn("A categoria correta é BUG, pois há um erro no sistema.");

        TicketCategory category = classifier.classify("Falha ao salvar", "Ocorre um erro ao clicar em salvar");

        assertThat(category).isEqualTo(TicketCategory.BUG);
    }

    @Test
    void deveCairParaRegrasQuandoRespostaNaoReconhecida() {
        when(callResponseSpec.content()).thenReturn("não sei classificar isso");

        TicketCategory category = classifier.classify("Dúvida sobre cobrança", "Fui cobrado no cartão");

        assertThat(category).isEqualTo(TicketCategory.BILLING); // fallback via RuleBasedClassifier
    }

    @Test
    void deveCairParaRegrasQuandoIaLancaExcecao() {
        when(requestSpec.call()).thenThrow(new RuntimeException("timeout"));

        TicketCategory category = classifier.classify("Sistema com erro", "Aparece uma exception ao abrir");

        assertThat(category).isEqualTo(TicketCategory.BUG); // fallback via RuleBasedClassifier
    }

    @Test
    void deveExporModoAi() {
        assertThat(classifier.mode()).isEqualTo(ClassifierMode.AI);
    }
}
