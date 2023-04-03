package com.cafe.storekeeper.infrastructure.domain.bill.rest.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.cafe.storekeeper.helper.enumerated.EProductType;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class ConceptBillDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("code")
    private String code;

    @JsonProperty("concept")
    private String concept;

    @JsonProperty("description")
    private String description;

    @JsonProperty("measurement_unit")
    private String measurementUnit;

    @JsonProperty("product_type")
    private EProductType productType;

    @JsonProperty("quantity")
    private long quantity;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

}