package com.cafe.storekeeper.infrastructure.adapter;

import java.util.List;
import java.util.Map;

import com.cafe.storekeeper.infrastructure.domain.provider.rest.model.dto.ProviderDTO;
import com.cafe.storekeeper.infrastructure.exception.ModelException;

public interface IProviderService {

    List<ProviderDTO> find(Map<String, String> filters) throws ModelException;

    ProviderDTO findById(String id) throws ModelException;

    ProviderDTO save(ProviderDTO dto) throws ModelException;

}