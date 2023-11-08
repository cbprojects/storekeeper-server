package com.cafe.storekeeper.infrastructure.domain.product_category.validator;

import org.apache.commons.lang3.StringUtils;

import com.cafe.storekeeper.helper.enums.EMapApiErrors;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;
import com.cafe.storekeeper.infrastructure.domain.product_category.rest.model.dto.ProductCategoryDTO;

public class ProductCategoryValidator {

    private ProductCategoryValidator() {
    }

    public static StandardErrorResponse isValidProductCategoryDTO(ProductCategoryDTO dto) {
        StandardErrorResponse result = null;

        if (dto == null) {
            result = new StandardErrorResponse(EMapApiErrors.ERROR_DATA_NOT_FOUND);
        } else {
            if (StringUtils.isBlank(dto.getCode())) {
                result = new StandardErrorResponse(EMapApiErrors.ERROR_DATA_NOT_FOUND);
            }
            if (StringUtils.isBlank(dto.getName())) {
                result = new StandardErrorResponse(EMapApiErrors.ERROR_DATA_NOT_FOUND);
            }
            if (StringUtils.isBlank(dto.getDescription())) {
                result = new StandardErrorResponse(EMapApiErrors.ERROR_DATA_NOT_FOUND);
            }
        }

        return result;
    }

}
