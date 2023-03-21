package com.cafe.storekeeper.helper.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.cafe.storekeeper.helper.constant.ConstantsMessage;
import com.cafe.storekeeper.helper.enumerated.EMapApiErrors;
import com.cafe.storekeeper.infrastructure.adapter.model.PaginationResponse;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardPaginationRestResponse;
import com.cafe.storekeeper.infrastructure.exception.RequestException;

public class UtilResponse {

    private UtilResponse() {

    }

    /**
     * Fill the response with data
     * 
     * @param object
     * @param recordCount
     * @param totalCount
     * @return StandardPaginationRestResponse
     */
    public static StandardPaginationRestResponse fillResponsePagination(Object object, long recordCount,
            long totalCount) {
        StandardPaginationRestResponse response = new StandardPaginationRestResponse();
        PaginationResponse pagination = new PaginationResponse();
        pagination.setRecordCount(recordCount);
        pagination.setTotalCount(totalCount);
        if (object != null) {
            response.setResult(object);
            response.setPagination(pagination);
        }

        return response;
    }

    /**
     * Build object for errors from code name
     * 
     * @param code
     * @return StandardErrorResponse
     */
    public static StandardErrorResponse buildApiError(String code) {
        return EMapApiErrors.findByCode(code);
    }

    public static StandardErrorResponse buildApiError(String code, String detail) {
        StandardErrorResponse errorDTO = EMapApiErrors.findByCode(code);
        if (detail != null && !detail.isEmpty()) {
            errorDTO.setDetail(detail);
        }
        return errorDTO;
    }

    /**
     * Build object for errors from a exception
     * 
     * @param exception
     * @return StandardErrorResponse
     */
    public static StandardErrorResponse buildApiError(Exception exception, String code) {
        StandardErrorResponse apiError = new StandardErrorResponse();
        if (exception != null) {
            apiError.setDetail(exception.getMessage() + " Localized : " + exception.getLocalizedMessage());
        }
        apiError.setMessage(ConstantsMessage.ERROR_DEFAULT);
        apiError.setCode(code);
        return apiError;
    }

    public static StandardErrorResponse buildApiError(RequestException exception, String code) {
        StandardErrorResponse apiError = new StandardErrorResponse();
        apiError.setCode(code);
        apiError.setDetail(ConstantsMessage.ERROR_DEFAULT);
        if (exception != null && exception.getMessage() != null) {
            apiError.setMessage(exception.getMessage());
        }
        return apiError;
    }

    /**
     * Build criteria params from query params of request
     * 
     * @param request
     * @return Map
     * @throws RequestException
     */
    public static Map<String, String> buildCriteriaParams(HttpServletRequest request) throws RequestException {
        Map<String, String> queryCriteria = new HashMap<>();

        if (request.getQueryString() != null) {
            String queryString;

            try {
                queryString = URLDecoder.decode(request.getQueryString(), StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException error) {
                throw new RequestException(error.getMessage());
            }

            if (StringUtils.isNotBlank(queryString)) {
                for (String pair : queryString.split("&")) {
                    String[] keyValue = pair.split("=");
                    if (keyValue.length == 2) {
                        queryCriteria.put(keyValue[0], keyValue[1]);
                    } else {
                        throw new RequestException(EMapApiErrors.ERROR_QUERY_PARAMS);
                    }
                }
            }
        }

        return queryCriteria;
    }

}