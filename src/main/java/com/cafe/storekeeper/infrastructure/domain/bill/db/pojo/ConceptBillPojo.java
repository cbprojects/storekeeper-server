package com.cafe.storekeeper.infrastructure.domain.bill.db.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.Field;

import com.cafe.storekeeper.helper.enums.EProductType;

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
public class ConceptBillPojo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Field("code")
    private String code;

    @Field("concept")
    private String concept;

    @Field("description")
    private String description;

    @Field("measurement_unit")
    private String measurementUnit;

    @Field("product_type")
    private EProductType productType;

    @Field("provider")
    private PersonBillPojo provider;

    @Field("quantity")
    private long quantity;

    @Field("amount")
    private BigDecimal amount;

    @Field("total_amount")
    private BigDecimal totalAmount;

}