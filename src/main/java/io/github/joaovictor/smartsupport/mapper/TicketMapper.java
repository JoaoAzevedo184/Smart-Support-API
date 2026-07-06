package io.github.joaovictor.smartsupport.mapper;

import io.github.joaovictor.smartsupport.dto.ticket.TicketResponse;
import io.github.joaovictor.smartsupport.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TicketMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "clientName", source = "client.name")
    @Mapping(target = "assignedTeamId", source = "assignedTeam.id")
    @Mapping(target = "assignedTeamName", source = "assignedTeam.name")
    @Mapping(target = "assignedUserId", source = "assignedUser.id")
    @Mapping(target = "assignedUserName", source = "assignedUser.name")
    TicketResponse toResponse(Ticket ticket);
}
