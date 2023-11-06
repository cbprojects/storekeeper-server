package com.cafe.storekeeper.infrastructure.adapter;

import java.util.List;
import java.util.Map;

import com.cafe.storekeeper.infrastructure.domain.employee.rest.model.dto.EmployeeDTO;
import com.cafe.storekeeper.infrastructure.exception.ModelException;

public interface IEmployeeService {

    List<EmployeeDTO> find(Map<String, String> filters) throws ModelException;

    EmployeeDTO findById(String id) throws ModelException;

    EmployeeDTO save(EmployeeDTO dto) throws ModelException;

    boolean delete(String id) throws ModelException;

}