package com.cafe.storekeeper.infrastructure.domain.client.db.persistence;

import java.io.Serializable;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.cafe.storekeeper.helper.constant.ConstantsTableNames;
import com.cafe.storekeeper.helper.enumerated.EDocumentType;
import com.cafe.storekeeper.infrastructure.adapter.model.AbstractEntity;
import com.cafe.storekeeper.infrastructure.domain.client.db.pojo.ContactPersonPojo;

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
@Document(collection = ConstantsTableNames.CLIENTS)
public class ClientEntity extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = 1430828715777440238L;

    @Field("name")
    private String name;

    @Field("info")
    private ContactPersonPojo info;

    @Field("document_number")
    @Indexed(unique = true)
    private String documentNumber;

    @Field("document_type")
    private EDocumentType documentType;

    @Field("image")
    private String image;

}
