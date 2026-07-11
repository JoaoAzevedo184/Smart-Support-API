package io.github.joaovictor.smartsupport.command;

import io.github.joaovictor.smartsupport.dto.ticket.TicketResponse;

/**
 * Command — contrato de uma ação sobre o chamado (fechar, reabrir, atribuir).
 * Encapsula a ação como objeto, desacoplando quem a solicita
 * ({@code TicketController}) de quem a executa ({@code TicketFacade}).
 */
public interface TicketCommand {

    TicketResponse execute();
}
