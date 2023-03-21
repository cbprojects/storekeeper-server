package com.cafe.storekeeper.infrastructure.domain.bill.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.cafe.storekeeper.infrastructure.domain.bill.db.persistence.BillEntity;
import com.cafe.storekeeper.infrastructure.domain.bill.rest.model.dto.BillDTO;

@Mapper(componentModel = "spring")
public interface BillMapper {

    BillDTO toDTO(BillEntity entity);

    BillEntity toEntity(BillDTO dto);

    List<BillDTO> map(List<BillEntity> entities);

}
