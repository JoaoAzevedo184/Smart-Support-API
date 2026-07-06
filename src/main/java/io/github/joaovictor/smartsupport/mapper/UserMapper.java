package io.github.joaovictor.smartsupport.mapper;

import io.github.joaovictor.smartsupport.dto.user.UserRequest;
import io.github.joaovictor.smartsupport.dto.user.UserResponse;
import io.github.joaovictor.smartsupport.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface UserMapper {

    @Mapping(target = "supportTeam", ignore = true)
    User toEntity(UserRequest request);

    @Mapping(target = "supportTeamId", source = "supportTeam.id")
    @Mapping(target = "supportTeamName", source = "supportTeam.name")
    UserResponse toResponse(User user);

    @Mapping(target = "supportTeam", ignore = true)
    void updateEntityFromRequest(UserRequest request, @MappingTarget User user);
}
