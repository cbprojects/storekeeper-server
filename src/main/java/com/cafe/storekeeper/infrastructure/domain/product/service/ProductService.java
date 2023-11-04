package com.cafe.storekeeper.infrastructure.domain.product.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cafe.storekeeper.helper.enumerated.EMapApiErrors;
import com.cafe.storekeeper.infrastructure.adapter.IProductService;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;
import com.cafe.storekeeper.infrastructure.domain.product.db.persistence.ProductEntity;
import com.cafe.storekeeper.infrastructure.domain.product.db.repository.ProductRepository;
import com.cafe.storekeeper.infrastructure.domain.product.mapper.ProductMapper;
import com.cafe.storekeeper.infrastructure.domain.product.rest.model.dto.ProductDTO;
import com.cafe.storekeeper.infrastructure.domain.product.validator.ProductValidator;
import com.cafe.storekeeper.infrastructure.exception.ModelException;

@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductMapper mapper;

    @Autowired
    private ProductRepository repository;

    @Override
    public ProductDTO findById(String id) throws ModelException {
        try {
            ProductDTO result = null;
            Optional<ProductEntity> optional = repository.findById(id);
            if (optional.isPresent()) {
                result = mapper.toDTO(optional.get());
            }

            return result;
        } catch (Exception excepcion) {
            throw new ModelException(excepcion.getMessage());
        }
    }

    @Override
    public List<ProductDTO> find(Map<String, String> filters) throws ModelException {
        try {
            List<ProductDTO> result = new ArrayList<>();
            List<ProductEntity> entities = repository.findAll();
            if (!entities.isEmpty()) {
                result = mapper.map(entities);
            }

            return result;
        } catch (Exception excepcion) {
            throw new ModelException(excepcion.getMessage());
        }
    }

    @Override
    public ProductDTO save(ProductDTO dto) throws ModelException {
        // Request validation
        StandardErrorResponse codeError = ProductValidator.isValidProductDTO(dto);
        if (codeError != null) {
            throw new ModelException(codeError);
        }

        try {
            // Mapper to entity
            ProductEntity entity = mapper.toEntity(dto);

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
            Optional<ProductEntity> optional = repository.findById(id);
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

    private void setAuditValues(ProductEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateDate(now);
        entity.setUpdateDate(now);
    }

}
