package com.cafe.storekeeper.infrastructure.domain.employee.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.cafe.storekeeper.infrastructure.domain.employee.db.persistence.EmployeeEntity;
import com.cafe.storekeeper.infrastructure.domain.employee.rest.model.dto.EmployeeDTO;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeDTO toDTO(EmployeeEntity entity);

    EmployeeEntity toEntity(EmployeeDTO dto);

    List<EmployeeDTO> map(List<EmployeeEntity> entities);

}
