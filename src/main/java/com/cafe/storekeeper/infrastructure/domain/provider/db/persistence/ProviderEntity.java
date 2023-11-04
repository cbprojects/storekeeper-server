package com.cafe.storekeeper.infrastructure.domain.provider.db.persistence;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.cafe.storekeeper.helper.constant.ConstantsTableNames;
import com.cafe.storekeeper.helper.enumerated.EDocumentType;
import com.cafe.storekeeper.infrastructure.adapter.model.AbstractEntity;
import com.cafe.storekeeper.infrastructure.domain.provider.db.pojo.ContactProviderPersonPojo;

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
@Document(collection = ConstantsTableNames.PROVIDERS)
public class ProviderEntity extends AbstractEntity {

    @Field("name")
    private String name;

    @Field("info")
    private ContactProviderPersonPojo info;

    @Field("document_number")
    @Indexed(unique = true)
    private String documentNumber;

    @Field("document_type")
    private EDocumentType documentType;

    @Field("image")
    private String image;

}
