package com.cafe.storekeeper.infrastructure.domain.assignment.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cafe.storekeeper.infrastructure.adapter.IAssignmentService;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;
import com.cafe.storekeeper.infrastructure.domain.assignment.db.persistence.AssignmentEntity;
import com.cafe.storekeeper.infrastructure.domain.assignment.db.repository.AssignmentRepository;
import com.cafe.storekeeper.infrastructure.domain.assignment.mapper.AssignmentMapper;
import com.cafe.storekeeper.infrastructure.domain.assignment.rest.model.dto.AssignmentDTO;
import com.cafe.storekeeper.infrastructure.domain.assignment.validator.AssignmentValidator;
import com.cafe.storekeeper.infrastructure.exception.ModelException;

@Service
public class AssignmentService implements IAssignmentService {

    @Autowired
    private AssignmentMapper mapper;

    @Autowired
    private AssignmentRepository repository;

    @Override
    public AssignmentDTO findByEmployeeId(String employeeId) throws ModelException {
        try {
            AssignmentDTO result = null;
            Optional<AssignmentEntity> optional = repository.findByEmployeeId(employeeId);
            if (optional.isPresent()) {
                result = mapper.toDTO(optional.get());
            }

            return result;
        } catch (Exception excepcion) {
            throw new ModelException(excepcion.getMessage());
        }
    }

    @Override
    public AssignmentDTO save(AssignmentDTO dto) throws ModelException {
        // Request validation
        StandardErrorResponse codeError = AssignmentValidator.isValidAssignmentDTO(dto);
        if (codeError != null) {
            throw new ModelException(codeError);
        }

        try {
            // Mapper to entity
            AssignmentEntity entity = mapper.toEntity(dto);

            // Set audit values
            this.setAuditValues(entity);

            // Save entity
            entity = this.repository.save(entity);

            return mapper.toDTO(entity);
        } catch (Exception excepcion) {
            throw new ModelException(excepcion.getMessage());
        }
    }

    private void setAuditValues(AssignmentEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateDate(now);
        entity.setUpdateDate(now);
    }

}