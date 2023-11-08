package com.cafe.storekeeper.infrastructure.domain.bill.validator;

import com.cafe.storekeeper.helper.enums.EMapApiErrors;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;
import com.cafe.storekeeper.infrastructure.domain.bill.rest.model.dto.BillDTO;
import com.cafe.storekeeper.infrastructure.domain.bill.rest.model.request.StatusBillRequest;

public class BillValidator {

    private BillValidator() {
    }

    public static StandardErrorResponse isValidBillDTO(BillDTO dto) {
        StandardErrorResponse result = null;

        if (dto == null) {
            result = new StandardErrorResponse(EMapApiErrors.ERROR_DATA_NOT_FOUND);
        }

        return result;
    }

    public static StandardErrorResponse isValidStatusBillRequest(StatusBillRequest request) {
        StandardErrorResponse result = null;

        if (request == null) {
            result = new StandardErrorResponse(EMapApiErrors.ERROR_DATA_NOT_FOUND);
        }

        return result;
    }

}
