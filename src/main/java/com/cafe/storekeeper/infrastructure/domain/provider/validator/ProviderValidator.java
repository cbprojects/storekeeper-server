package com.cafe.storekeeper.infrastructure.domain.provider.validator;

import com.cafe.storekeeper.helper.enumerated.EMapApiErrors;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;
import com.cafe.storekeeper.infrastructure.domain.provider.rest.model.dto.ProviderDTO;

public class ProviderValidator {

    private ProviderValidator() {
    }

    public static StandardErrorResponse isValidProviderDTO(ProviderDTO dto) {
        StandardErrorResponse result = null;

        if (dto == null) {
            result = new StandardErrorResponse(EMapApiErrors.ERROR_DATA_NOT_FOUND);
        }

        return result;
    }

}
