package com.cafe.storekeeper.infrastructure.domain.product_category.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cafe.storekeeper.helper.enums.EMapApiErrors;
import com.cafe.storekeeper.infrastructure.adapter.IProductCategoryService;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;
import com.cafe.storekeeper.infrastructure.domain.product_category.db.persistence.ProductCategoryEntity;
import com.cafe.storekeeper.infrastructure.domain.product_category.db.repository.ProductCategoryRepository;
import com.cafe.storekeeper.infrastructure.domain.product_category.mapper.ProductCategoryMapper;
import com.cafe.storekeeper.infrastructure.domain.product_category.rest.model.dto.ProductCategoryDTO;
import com.cafe.storekeeper.infrastructure.domain.product_category.validator.ProductCategoryValidator;
import com.cafe.storekeeper.infrastructure.exception.ModelException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductCategoryService implements IProductCategoryService {

    private ProductCategoryMapper mapper;

    private ProductCategoryRepository repository;

    @Override
    public ProductCategoryDTO findById(String id) throws ModelException {
        try {
            ProductCategoryDTO result = null;
            Optional<ProductCategoryEntity> optional = repository.findById(id);
            if (optional.isPresent()) {
                result = mapper.toDTO(optional.get());
            }

            return result;
        } catch (Exception excepcion) {
            throw new ModelException(excepcion.getMessage());
        }
    }

    @Override
    public List<ProductCategoryDTO> find(Map<String, String> filters) throws ModelException {
        try {
            List<ProductCategoryDTO> result = new ArrayList<>();
            List<ProductCategoryEntity> entities = repository.findAll();
            if (!entities.isEmpty()) {
                result = mapper.map(entities);
            }

            return result;
        } catch (Exception excepcion) {
            throw new ModelException(excepcion.getMessage());
        }
    }

    @Override
    public ProductCategoryDTO save(ProductCategoryDTO dto) throws ModelException {
        // Request validation
        StandardErrorResponse codeError = ProductCategoryValidator.isValidProductCategoryDTO(dto);
        if (codeError != null) {
            throw new ModelException(codeError);
        }

        try {
            // Mapper to entity
            ProductCategoryEntity entity = mapper.toEntity(dto);

            // Set audit values
            this.setAuditValues(entity);

            // Save entity
            entity = this.repository.save(entity);

            return mapper.toDTO(entity);
        } catch (Exception excepcion) {
            throw new ModelException(excepcion.getMessage());
        }
    }

    @Override
    public boolean delete(String id) throws ModelException {
        boolean result = false;
        try {
            Optional<ProductCategoryEntity> optional = repository.findById(id);
            if (optional.isPresent()) {
                repository.delete(optional.get());
                result = true;
            } else {
                throw new ModelException(new StandardErrorResponse(EMapApiErrors.ERROR_DATA_NOT_FOUND));
            }
        } catch (Exception excepcion) {
            throw new ModelException(excepcion.getMessage());
        }

        return result;
    }

    private void setAuditValues(ProductCategoryEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateDate(now);
        entity.setUpdateDate(now);
    }

}
