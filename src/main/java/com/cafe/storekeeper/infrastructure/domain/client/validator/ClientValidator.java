package com.cafe.storekeeper.infrastructure.domain.client.validator;

import com.cafe.storekeeper.helper.enums.EMapApiErrors;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;
import com.cafe.storekeeper.infrastructure.domain.client.rest.model.dto.ClientDTO;

public class ClientValidator {

    private ClientValidator() {
    }

    public static StandardErrorResponse isValidClientDTO(ClientDTO dto) {
        StandardErrorResponse result = null;

        if (dto == null) {
            result = new StandardErrorResponse(EMapApiErrors.ERROR_DATA_NOT_FOUND);
        }

        return result;
    }

}
