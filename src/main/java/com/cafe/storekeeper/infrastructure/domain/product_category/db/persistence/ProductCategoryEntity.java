package com.cafe.storekeeper.infrastructure.domain.product_category.db.persistence;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.cafe.storekeeper.helper.constant.ConstantsTableNames;
import com.cafe.storekeeper.infrastructure.adapter.model.AbstractEntity;

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
@Document(collection = ConstantsTableNames.PRODUCTS_CATEGORIES)
public class ProductCategoryEntity extends AbstractEntity {

    @Field("code")
    @Indexed(unique = true)
    private String code;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("color")
    private String color;

    @Field("image")
    private String image;

}
