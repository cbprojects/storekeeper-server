package com.cafe.storekeeper.infrastructure.domain.bill.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cafe.storekeeper.infrastructure.adapter.IBillService;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;
import com.cafe.storekeeper.infrastructure.domain.bill.db.persistence.BillEntity;
import com.cafe.storekeeper.infrastructure.domain.bill.db.repository.BillRepository;
import com.cafe.storekeeper.infrastructure.domain.bill.mapper.BillMapper;
import com.cafe.storekeeper.infrastructure.domain.bill.rest.model.dto.BillDTO;
import com.cafe.storekeeper.infrastructure.domain.bill.rest.model.request.StatusBillRequest;
import com.cafe.storekeeper.infrastructure.domain.bill.validator.BillValidator;
import com.cafe.storekeeper.infrastructure.exception.ModelException;

@Service
public class BillService implements IBillService {

    @Autowired
    private BillMapper mapper;

    @Autowired
    private BillRepository repository;

    @Override
    public BillDTO findById(String id) throws ModelException {
        try {
            BillDTO result = null;
            Optional<BillEntity> optional = repository.findById(id);
            if (optional.isPresent()) {
                result = mapper.toDTO(optional.get());
            }

            return result;
        } catch (Exception excepcion) {
            throw new ModelException(excepcion.getMessage());
        }
    }

    @Override
    public List<BillDTO> find(Map<String, String> filters) throws ModelException {
        try {
            List<BillDTO> result = new ArrayList<>();
            List<BillEntity> entities = repository.findAll();
            if (!entities.isEmpty()) {
                result = mapper.map(entities);
            }

            return result;
        } catch (Exception excepcion) {
            throw new ModelException(excepcion.getMessage());
        }
    }

    @Override
    public BillDTO save(BillDTO dto) throws ModelException {
        // Request validation
        StandardErrorResponse codeError = BillValidator.isValidBillDTO(dto);
        if (codeError != null) {
            throw new ModelException(codeError);
        }

        // Mapper to entity
        BillEntity entity = mapper.toEntity(dto);

        // Set audit values
        this.setAuditValues(entity);

        // Save entity
        entity = this.repository.save(entity);

        return mapper.toDTO(entity);
    }

    @Override
    public BillDTO updateStatus(StatusBillRequest request) throws ModelException {
        BillDTO result = null;

        // Request validation
        StandardErrorResponse codeError = BillValidator.isValidStatusBillRequest(request);
        if (codeError != null) {
            throw new ModelException(codeError);
        }

        // Find entity
        Optional<BillEntity> optional = repository.findById(request.getId());
        if (optional.isPresent()) {
            BillEntity entity = optional.get();
            entity.setStatus(request.getStatus());
            entity.setUpdateDate(LocalDateTime.now());

            // Save entity
            result = mapper.toDTO(this.repository.save(entity));
        }

        return result;
    }

    private void setAuditValues(BillEntity billEntity) {
        LocalDateTime now = LocalDateTime.now();
        billEntity.setCreateDate(now);
        billEntity.setUpdateDate(now);
    }

}
