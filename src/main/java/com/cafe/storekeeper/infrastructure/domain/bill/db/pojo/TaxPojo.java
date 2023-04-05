package com.cafe.storekeeper.infrastructure.domain.bill.db.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.Field;

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
public class TaxPojo implements Serializable {
    private static final long serialVersionUID = 1430828715777440238L;

    @Field("description")
    private String description;

    @Field("amount")
    private BigDecimal amount;

}