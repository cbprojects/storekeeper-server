package com.cafe.storekeeper.infrastructure.domain.client.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.cafe.storekeeper.infrastructure.domain.client.db.persistence.ClientEntity;
import com.cafe.storekeeper.infrastructure.domain.client.rest.model.dto.ClientDTO;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientDTO toDTO(ClientEntity entity);

    ClientEntity toEntity(ClientDTO dto);

    List<ClientDTO> map(List<ClientEntity> entities);

}
