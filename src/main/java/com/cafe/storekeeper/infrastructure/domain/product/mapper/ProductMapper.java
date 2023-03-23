package com.cafe.storekeeper.infrastructure.domain.product.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.cafe.storekeeper.infrastructure.domain.product.db.persistence.ProductEntity;
import com.cafe.storekeeper.infrastructure.domain.product.rest.model.dto.ProductDTO;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO toDTO(ProductEntity entity);

    ProductEntity toEntity(ProductDTO dto);

    List<ProductDTO> map(List<ProductEntity> entities);

}
