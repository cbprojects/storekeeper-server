package com.cafe.storekeeper.infrastructure.domain.company.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.cafe.storekeeper.infrastructure.domain.company.db.persistence.CompanyEntity;
import com.cafe.storekeeper.infrastructure.domain.company.rest.model.dto.CompanyDTO;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    CompanyDTO toDTO(CompanyEntity entity);

    CompanyEntity toEntity(CompanyDTO dto);

    List<CompanyDTO> map(List<CompanyEntity> entities);

}
