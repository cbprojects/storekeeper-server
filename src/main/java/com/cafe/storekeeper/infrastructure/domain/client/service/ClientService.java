package com.cafe.storekeeper.infrastructure.domain.client.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cafe.storekeeper.infrastructure.adapter.IClientService;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;
import com.cafe.storekeeper.infrastructure.domain.client.db.persistence.ClientEntity;
import com.cafe.storekeeper.infrastructure.domain.client.db.repository.ClientRepository;
import com.cafe.storekeeper.infrastructure.domain.client.mapper.ClientMapper;
import com.cafe.storekeeper.infrastructure.domain.client.rest.model.dto.ClientDTO;
import com.cafe.storekeeper.infrastructure.domain.client.validator.ClientValidator;
import com.cafe.storekeeper.infrastructure.exception.ModelException;

@Service
public class ClientService implements IClientService {

    @Autowired
    private ClientMapper mapper;

    @Autowired
    private ClientRepository repository;

    @Override
    public ClientDTO findById(String id) throws ModelException {
        try {
            ClientDTO result = null;
            Optional<ClientEntity> optional = repository.findById(id);
            if (optional.isPresent()) {
                result = mapper.toDTO(optional.get());
            }

            return result;
        } catch (Exception excepcion) {
            throw new ModelException(excepcion.getMessage());
        }
    }

    @Override
    public List<ClientDTO> find(Map<String, String> filters) throws ModelException {
        try {
            List<ClientDTO> result = new ArrayList<>();
            List<ClientEntity> entities = repository.findAll();
            if (!entities.isEmpty()) {
                result = mapper.map(entities);
            }

            return result;
        } catch (Exception excepcion) {
            throw new ModelException(excepcion.getMessage());
        }
    }

    @Override
    public ClientDTO save(ClientDTO dto) throws ModelException {
        // Request validation
        StandardErrorResponse codeError = ClientValidator.isValidClientDTO(dto);
        if (codeError != null) {
            throw new ModelException(codeError);
        }

        // Mapper to entity
        ClientEntity entity = mapper.toEntity(dto);

        // Set audit values
        this.setAuditValues(entity);

        // Save entity
        entity = this.repository.save(entity);

        return mapper.toDTO(entity);
    }

    private void setAuditValues(ClientEntity clientEntity) {
        LocalDateTime now = LocalDateTime.now();
        clientEntity.setCreateDate(now);
        clientEntity.setUpdateDate(now);
    }

}
