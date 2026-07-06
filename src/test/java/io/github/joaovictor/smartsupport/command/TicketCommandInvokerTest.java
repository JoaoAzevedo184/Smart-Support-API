package io.github.joaovictor.smartsupport.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.joaovictor.smartsupport.dto.ticket.TicketResponse;
import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import io.github.joaovictor.smartsupport.entity.enums.TicketStatus;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TicketCommandInvokerTest {

    private final TicketCommandInvoker invoker = new TicketCommandInvoker();

    @Test
    void deveExecutarComandoERetornarResultado() {
        TicketResponse expected = new TicketResponse(
                UUID.randomUUID(), "t", "d", TicketStatus.CLOSED, null, TicketCategory.BUG,
                UUID.randomUUID(), "cliente", null, null, null, null, null, null, null);
        TicketCommand command = mock(TicketCommand.class);
        when(command.execute()).thenReturn(expected);

        TicketResponse result = invoker.run(command);

        assertThat(result).isEqualTo(expected);
        verify(command).execute();
    }
}
