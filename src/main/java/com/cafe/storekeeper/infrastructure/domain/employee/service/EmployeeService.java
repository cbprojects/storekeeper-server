package com.cafe.storekeeper.infrastructure.domain.employee.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cafe.storekeeper.helper.enumerated.EMapApiErrors;
import com.cafe.storekeeper.infrastructure.adapter.IEmployeeService;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;
import com.cafe.storekeeper.infrastructure.domain.employee.db.persistence.EmployeeEntity;
import com.cafe.storekeeper.infrastructure.domain.employee.db.repository.EmployeeRepository;
import com.cafe.storekeeper.infrastructure.domain.employee.mapper.EmployeeMapper;
import com.cafe.storekeeper.infrastructure.domain.employee.rest.model.dto.EmployeeDTO;
import com.cafe.storekeeper.infrastructure.domain.employee.validator.EmployeeValidator;
import com.cafe.storekeeper.infrastructure.exception.ModelException;

@Service
public class EmployeeService implements IEmployeeService {

    @Autowired
    private EmployeeMapper mapper;

    @Autowired
    private EmployeeRepository repository;

    @Override
    public EmployeeDTO findById(String id) throws ModelException {
        try {
            EmployeeDTO result = null;
            Optional<EmployeeEntity> optional = repository.findById(id);
            if (optional.isPresent()) {
                result = mapper.toDTO(optional.get());
            }

            return result;
        } catch (Exception excepcion) {
            throw new ModelException(excepcion.getMessage());
        }
    }

    @Override
    public List<EmployeeDTO> find(Map<String, String> filters) throws ModelException {
        try {
            List<EmployeeDTO> result = new ArrayList<>();
            List<EmployeeEntity> entities = repository.findAll();
            if (!entities.isEmpty()) {
                result = mapper.map(entities);
            }

            return result;
        } catch (Exception excepcion) {
            throw new ModelException(excepcion.getMessage());
        }
    }

    @Override
    public EmployeeDTO save(EmployeeDTO dto) throws ModelException {
        // Request validation
        StandardErrorResponse codeError = EmployeeValidator.isValidEmployeeDTO(dto);
        if (codeError != null) {
            throw new ModelException(codeError);
        }

        try {
            // Mapper to entity
            EmployeeEntity entity = mapper.toEntity(dto);

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
            Optional<EmployeeEntity> optional = repository.findById(id);
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

    private void setAuditValues(EmployeeEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateDate(now);
        entity.setUpdateDate(now);
    }

}
