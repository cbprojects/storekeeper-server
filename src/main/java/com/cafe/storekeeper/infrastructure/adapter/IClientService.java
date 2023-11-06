package com.cafe.storekeeper.infrastructure.adapter;

import java.util.List;
import java.util.Map;

import com.cafe.storekeeper.infrastructure.domain.client.rest.model.dto.ClientDTO;
import com.cafe.storekeeper.infrastructure.exception.ModelException;

public interface IClientService {

    List<ClientDTO> find(Map<String, String> filters) throws ModelException;

    ClientDTO findById(String id) throws ModelException;

    ClientDTO save(ClientDTO dto) throws ModelException;

    boolean delete(String id) throws ModelException;

}