package com.cafe.storekeeper.infrastructure.domain.product_category.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.cafe.storekeeper.infrastructure.domain.product_category.db.persistence.ProductCategoryEntity;
import com.cafe.storekeeper.infrastructure.domain.product_category.rest.model.dto.ProductCategoryDTO;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {

    ProductCategoryDTO toDTO(ProductCategoryEntity entity);

    ProductCategoryEntity toEntity(ProductCategoryDTO dto);

    List<ProductCategoryDTO> map(List<ProductCategoryEntity> entities);

}
