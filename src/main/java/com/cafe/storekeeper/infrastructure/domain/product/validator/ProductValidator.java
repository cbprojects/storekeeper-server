package com.cafe.storekeeper.infrastructure.domain.product.validator;

import com.cafe.storekeeper.helper.enums.EMapApiErrors;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;
import com.cafe.storekeeper.infrastructure.domain.product.rest.model.dto.ProductDTO;

public class ProductValidator {

    private ProductValidator() {
    }

    public static StandardErrorResponse isValidProductDTO(ProductDTO dto) {
        StandardErrorResponse result = null;

        if (dto == null) {
            result = new StandardErrorResponse(EMapApiErrors.ERROR_DATA_NOT_FOUND);
        }

        return result;
    }

}
