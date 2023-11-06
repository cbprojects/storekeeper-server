package com.cafe.storekeeper.infrastructure.adapter;

import java.util.List;
import java.util.Map;

import com.cafe.storekeeper.infrastructure.domain.product_category.rest.model.dto.ProductCategoryDTO;
import com.cafe.storekeeper.infrastructure.exception.ModelException;

public interface IProductCategoryService {

    List<ProductCategoryDTO> find(Map<String, String> filters) throws ModelException;

    ProductCategoryDTO findById(String id) throws ModelException;

    ProductCategoryDTO save(ProductCategoryDTO dto) throws ModelException;

    boolean delete(String id) throws ModelException;

}