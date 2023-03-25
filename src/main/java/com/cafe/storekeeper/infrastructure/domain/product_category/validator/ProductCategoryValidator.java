package com.cafe.storekeeper.infrastructure.domain.product_category.validator;

import com.cafe.storekeeper.helper.enumerated.EMapApiErrors;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;
import com.cafe.storekeeper.infrastructure.domain.product_category.rest.model.dto.ProductCategoryDTO;

public class ProductCategoryValidator {

    private ProductCategoryValidator() {
    }

    public static StandardErrorResponse isValidProductCategoryDTO(ProductCategoryDTO dto) {
        StandardErrorResponse result = null;

        if (dto == null) {
            result = new StandardErrorResponse(EMapApiErrors.ERROR_DATA_NOT_FOUND);
        }

        return result;
    }

}
