package com.cafe.storekeeper.infrastructure.adapter.model;

import java.io.Serializable;

import com.cafe.storekeeper.helper.enums.EMapApiErrors;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardErrorResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("detail")
    private String detail;

    public StandardErrorResponse(EMapApiErrors apiErrorEnum) {
        this.code = apiErrorEnum.getCode();
        this.detail = apiErrorEnum.getDetail();
        this.message = apiErrorEnum.getMessage();
    }
}
