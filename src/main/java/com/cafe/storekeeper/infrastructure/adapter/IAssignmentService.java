package com.cafe.storekeeper.infrastructure.adapter;

import com.cafe.storekeeper.infrastructure.domain.assignment.rest.model.dto.AssignmentDTO;
import com.cafe.storekeeper.infrastructure.exception.ModelException;

public interface IAssignmentService {

    AssignmentDTO findByEmployeeId(String employeeId) throws ModelException;

    AssignmentDTO save(AssignmentDTO dto) throws ModelException;

}