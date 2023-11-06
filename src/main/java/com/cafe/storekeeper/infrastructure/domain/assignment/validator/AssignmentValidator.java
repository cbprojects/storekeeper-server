package com.cafe.storekeeper.infrastructure.domain.assignment.validator;

import com.cafe.storekeeper.helper.enumerated.EMapApiErrors;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;
import com.cafe.storekeeper.infrastructure.domain.assignment.rest.model.dto.AssignmentDTO;

public class AssignmentValidator {

    private AssignmentValidator() {
    }

    public static StandardErrorResponse isValidAssignmentDTO(AssignmentDTO dto) {
        StandardErrorResponse result = null;

        if (dto == null) {
            result = new StandardErrorResponse(EMapApiErrors.ERROR_DATA_NOT_FOUND);
        }

        return result;
    }

}
