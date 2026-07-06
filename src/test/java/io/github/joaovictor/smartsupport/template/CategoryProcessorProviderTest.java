package io.github.joaovictor.smartsupport.template;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class CategoryProcessorProviderTest {

    private final CategoryProcessorProvider provider = new CategoryProcessorProvider(
            List.of(new BugTicketProcessor(), new BillingTicketProcessor(), new SupportTicketProcessor()));

    @Test
    void deveResolverProcessorPelaCategoria() {
        CategoryProcessor processor = provider.getProcessor(TicketCategory.BUG);

        assertThat(processor).isInstanceOf(BugTicketProcessor.class);
    }

    @Test
    void deveLancarExcecaoParaCategoriaNaoSuportada() {
        CategoryProcessorProvider emptyProvider = new CategoryProcessorProvider(List.of());

        assertThatThrownBy(() -> emptyProvider.getProcessor(TicketCategory.BUG))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não suportada");
    }
}
