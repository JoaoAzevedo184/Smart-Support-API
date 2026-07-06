package io.github.joaovictor.smartsupport.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class TicketFactoryProviderTest {

    private final TicketFactoryProvider provider = new TicketFactoryProvider(
            List.of(new BugTicketFactory(), new BillingTicketFactory(), new SupportTicketFactory()));

    @Test
    void deveResolverFactoryPelaCategoria() {
        TicketFactory factory = provider.getFactory(TicketCategory.BUG);

        assertThat(factory).isInstanceOf(BugTicketFactory.class);
        assertThat(factory.category()).isEqualTo(TicketCategory.BUG);
    }

    @Test
    void deveLancarExcecaoParaCategoriaNaoSuportada() {
        TicketFactoryProvider emptyProvider = new TicketFactoryProvider(List.of());

        assertThatThrownBy(() -> emptyProvider.getFactory(TicketCategory.BUG))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não suportada");
    }
}
