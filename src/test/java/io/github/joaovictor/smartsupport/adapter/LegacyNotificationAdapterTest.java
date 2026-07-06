package io.github.joaovictor.smartsupport.adapter;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LegacyNotificationAdapterTest {

    @Mock
    private LegacyNotificationSystem legacyNotificationSystem;

    @Test
    void deveTraduzirChamadaModernaParaAssinaturaLegada() {
        LegacyNotificationAdapter adapter = new LegacyNotificationAdapter(legacyNotificationSystem);

        adapter.notify("cliente-123", "Chamado atualizado");

        verify(legacyNotificationSystem).dispatch("cliente-123", "Chamado atualizado", 1);
    }
}
