package io.github.joaovictor.smartsupport.mapper;

import io.github.joaovictor.smartsupport.dto.supportteam.SupportTeamRequest;
import io.github.joaovictor.smartsupport.dto.supportteam.SupportTeamResponse;
import io.github.joaovictor.smartsupport.entity.SupportTeam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/** Mapper MapStruct entre {@code SupportTeam} e seus DTOs. */
@Mapper
public interface SupportTeamMapper {

    @Mapping(target = "users", ignore = true)
    SupportTeam toEntity(SupportTeamRequest request);

    SupportTeamResponse toResponse(SupportTeam supportTeam);

    @Mapping(target = "users", ignore = true)
    void updateEntityFromRequest(SupportTeamRequest request, @MappingTarget SupportTeam supportTeam);
}
