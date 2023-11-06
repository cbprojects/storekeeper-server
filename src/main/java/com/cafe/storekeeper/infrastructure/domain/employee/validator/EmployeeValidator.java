package com.cafe.storekeeper.infrastructure.domain.employee.validator;

import com.cafe.storekeeper.helper.enumerated.EMapApiErrors;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;
import com.cafe.storekeeper.infrastructure.domain.employee.rest.model.dto.EmployeeDTO;

public class EmployeeValidator {

    private EmployeeValidator() {
    }

    public static StandardErrorResponse isValidEmployeeDTO(EmployeeDTO dto) {
        StandardErrorResponse result = null;

        if (dto == null) {
            result = new StandardErrorResponse(EMapApiErrors.ERROR_DATA_NOT_FOUND);
        }

        return result;
    }

}
