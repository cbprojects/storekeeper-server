package com.cafe.storekeeper.infrastructure.adapter;

import java.util.List;
import java.util.Map;

import com.cafe.storekeeper.infrastructure.domain.product.rest.model.dto.ProductDTO;
import com.cafe.storekeeper.infrastructure.exception.ModelException;

public interface IProductService {

    List<ProductDTO> find(Map<String, String> filters) throws ModelException;

    ProductDTO findById(String id) throws ModelException;

    ProductDTO save(ProductDTO dto) throws ModelException;

    boolean delete(String id) throws ModelException;

}