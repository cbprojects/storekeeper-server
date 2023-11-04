package com.cafe.storekeeper.infrastructure.domain.provider.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cafe.storekeeper.helper.enumerated.EMapApiErrors;
import com.cafe.storekeeper.infrastructure.adapter.IProviderService;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;
import com.cafe.storekeeper.infrastructure.domain.provider.db.persistence.ProviderEntity;
import com.cafe.storekeeper.infrastructure.domain.provider.db.repository.ProviderRepository;
import com.cafe.storekeeper.infrastructure.domain.provider.mapper.ProviderMapper;
import com.cafe.storekeeper.infrastructure.domain.provider.rest.model.dto.ProviderDTO;
import com.cafe.storekeeper.infrastructure.domain.provider.validator.ProviderValidator;
import com.cafe.storekeeper.infrastructure.exception.ModelException;

@Service
public class ProviderService implements IProviderService {

    @Autowired
    private ProviderMapper mapper;

    @Autowired
    private ProviderRepository repository;

    @Override
    public ProviderDTO findById(String id) throws ModelException {
        try {
            ProviderDTO result = null;
            Optional<ProviderEntity> optional = repository.findById(id);
            if (optional.isPresent()) {
                result = mapper.toDTO(optional.get());
            }

            return result;
        } catch (Exception excepcion) {
            throw new ModelException(excepcion.getMessage());
        }
    }

    @Override
    public List<ProviderDTO> find(Map<String, String> filters) throws ModelException {
        try {
            List<ProviderDTO> result = new ArrayList<>();
            List<ProviderEntity> entities = repository.findAll();
            if (!entities.isEmpty()) {
                result = mapper.map(entities);
            }

            return result;
        } catch (Exception excepcion) {
            throw new ModelException(excepcion.getMessage());
        }
    }

    @Override
    public ProviderDTO save(ProviderDTO dto) throws ModelException {
        // Request validation
        StandardErrorResponse codeError = ProviderValidator.isValidProviderDTO(dto);
        if (codeError != null) {
            throw new ModelException(codeError);
        }

        try {
            // Mapper to entity
            ProviderEntity entity = mapper.toEntity(dto);

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
            Optional<ProviderEntity> optional = repository.findById(id);
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

    private void setAuditValues(ProviderEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateDate(now);
        entity.setUpdateDate(now);
    }

}
