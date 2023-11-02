package com.cafe.storekeeper.infrastructure.domain.product.db.persistence;

import java.math.BigDecimal;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.cafe.storekeeper.helper.constant.ConstantsTableNames;
import com.cafe.storekeeper.helper.enumerated.EProductType;
import com.cafe.storekeeper.infrastructure.adapter.model.AbstractEntity;
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
@Document(collection = ConstantsTableNames.PRODUCTS)
public class ProductEntity extends AbstractEntity {

    @Field("code")
    @Indexed(unique = true)
    private String code;

    @JsonProperty("name")
    private String name;

    @Field("description")
    private String description;

    @Field("stock_min")
    private long stockMin;

    @Field("stock_max")
    private long stockMax;

    @Field("type")
    private EProductType type;

    @Field("sale_price")
    private BigDecimal sale_price;

    @Field("price")
    private BigDecimal price;

    @Field("image")
    private String image;

}
