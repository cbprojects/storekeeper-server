package com.cafe.storekeeper.infrastructure.domain.provider.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.cafe.storekeeper.infrastructure.domain.provider.db.persistence.ProviderEntity;
import com.cafe.storekeeper.infrastructure.domain.provider.rest.model.dto.ProviderDTO;

@Mapper(componentModel = "spring")
public interface ProviderMapper {

    ProviderDTO toDTO(ProviderEntity entity);

    ProviderEntity toEntity(ProviderDTO dto);

    List<ProviderDTO> map(List<ProviderEntity> entities);

}
