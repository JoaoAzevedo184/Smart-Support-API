package io.github.joaovictor.smartsupport.mapper;

import io.github.joaovictor.smartsupport.dto.client.ClientRequest;
import io.github.joaovictor.smartsupport.dto.client.ClientResponse;
import io.github.joaovictor.smartsupport.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface ClientMapper {

    Client toEntity(ClientRequest request);

    ClientResponse toResponse(Client client);

    void updateEntityFromRequest(ClientRequest request, @MappingTarget Client client);
}
