package io.github.joaovictor.smartsupport.command;

import io.github.joaovictor.smartsupport.dto.ticket.TicketResponse;

public interface TicketCommand {

    TicketResponse execute();
}
