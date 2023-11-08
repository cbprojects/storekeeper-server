package com.cafe.storekeeper.infrastructure.domain.company.db.persistence;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.cafe.storekeeper.helper.constant.ConstantsTableNames;
import com.cafe.storekeeper.helper.enums.EDocumentType;
import com.cafe.storekeeper.infrastructure.adapter.model.AbstractEntity;
import com.cafe.storekeeper.infrastructure.domain.company.db.pojo.ContactCompanyPojo;

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
@Document(collection = ConstantsTableNames.COMPANIES)
public class CompanyEntity extends AbstractEntity {

    @Field("business_name")
    private String businessName;

    @Field("info")
    private ContactCompanyPojo info;

    @Field("document_number")
    @Indexed(unique = true)
    private String documentNumber;

    @Field("document_type")
    private EDocumentType documentType;

    @Field("image")
    private String image;

}
