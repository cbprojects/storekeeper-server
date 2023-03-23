package com.cafe.storekeeper.infrastructure.domain.product.rest.model.dto;

import java.math.BigDecimal;

import com.cafe.storekeeper.helper.enumerated.EProductType;
import com.cafe.storekeeper.infrastructure.adapter.model.AbstractDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO extends AbstractDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("stock_min")
    private long stockMin;

    @JsonProperty("stock_max")
    private long stockMax;

    @JsonProperty("type")
    private EProductType type;

    @JsonProperty("sale_price")
    private BigDecimal sale_price;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("image")
    private String image;

}
