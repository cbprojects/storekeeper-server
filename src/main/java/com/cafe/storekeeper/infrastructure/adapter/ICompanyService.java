package com.cafe.storekeeper.infrastructure.adapter;

import java.util.List;
import java.util.Map;

import com.cafe.storekeeper.infrastructure.domain.company.rest.model.dto.CompanyDTO;
import com.cafe.storekeeper.infrastructure.exception.ModelException;

public interface ICompanyService {

    List<CompanyDTO> find(Map<String, String> filters) throws ModelException;

    CompanyDTO findById(String id) throws ModelException;

    CompanyDTO save(CompanyDTO dto) throws ModelException;

    boolean delete(String id) throws ModelException;

}