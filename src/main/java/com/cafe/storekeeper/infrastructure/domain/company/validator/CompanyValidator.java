package com.cafe.storekeeper.infrastructure.domain.company.validator;

import com.cafe.storekeeper.helper.enums.EMapApiErrors;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;
import com.cafe.storekeeper.infrastructure.domain.company.rest.model.dto.CompanyDTO;

public class CompanyValidator {

    private CompanyValidator() {
    }

    public static StandardErrorResponse isValidCompanyDTO(CompanyDTO dto) {
        StandardErrorResponse result = null;

        if (dto == null) {
            result = new StandardErrorResponse(EMapApiErrors.ERROR_DATA_NOT_FOUND);
        }

        return result;
    }

}
