package com.cafe.storekeeper.infrastructure.domain.company.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cafe.storekeeper.infrastructure.adapter.ICompanyService;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;
import com.cafe.storekeeper.infrastructure.domain.company.db.persistence.CompanyEntity;
import com.cafe.storekeeper.infrastructure.domain.company.db.repository.CompanyRepository;
import com.cafe.storekeeper.infrastructure.domain.company.mapper.CompanyMapper;
import com.cafe.storekeeper.infrastructure.domain.company.rest.model.dto.CompanyDTO;
import com.cafe.storekeeper.infrastructure.domain.company.validator.CompanyValidator;
import com.cafe.storekeeper.infrastructure.exception.ModelException;

@Service
public class CompanyService implements ICompanyService {

    @Autowired
    private CompanyMapper mapper;

    @Autowired
    private CompanyRepository repository;

    @Override
    public CompanyDTO findById(String id) throws ModelException {
        try {
            CompanyDTO result = null;
            Optional<CompanyEntity> optional = repository.findById(id);
            if (optional.isPresent()) {
                result = mapper.toDTO(optional.get());
            }

            return result;
        } catch (Exception excepcion) {
            throw new ModelException(excepcion.getMessage());
        }
    }

    @Override
    public List<CompanyDTO> find(Map<String, String> filters) throws ModelException {
        try {
            List<CompanyDTO> result = new ArrayList<>();
            List<CompanyEntity> entities = repository.findAll();
            if (!entities.isEmpty()) {
                result = mapper.map(entities);
            }

            return result;
        } catch (Exception excepcion) {
            throw new ModelException(excepcion.getMessage());
        }
    }

    @Override
    public CompanyDTO save(CompanyDTO dto) throws ModelException {
        // Request validation
        StandardErrorResponse codeError = CompanyValidator.isValidCompanyDTO(dto);
        if (codeError != null) {
            throw new ModelException(codeError);
        }

        // Mapper to entity
        CompanyEntity entity = mapper.toEntity(dto);

        // Set audit values
        this.setAuditValues(entity);

        // Save entity
        entity = this.repository.save(entity);

        return mapper.toDTO(entity);
    }

    private void setAuditValues(CompanyEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateDate(now);
        entity.setUpdateDate(now);
    }

}
