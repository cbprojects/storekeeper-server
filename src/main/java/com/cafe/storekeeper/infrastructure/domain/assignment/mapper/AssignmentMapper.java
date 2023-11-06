package com.cafe.storekeeper.infrastructure.domain.assignment.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.cafe.storekeeper.infrastructure.domain.assignment.db.persistence.AssignmentEntity;
import com.cafe.storekeeper.infrastructure.domain.assignment.rest.model.dto.AssignmentDTO;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {

    AssignmentDTO toDTO(AssignmentEntity entity);

    AssignmentEntity toEntity(AssignmentDTO dto);

    List<AssignmentDTO> map(List<AssignmentEntity> entities);

}
