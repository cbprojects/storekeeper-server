package com.cafe.storekeeper.infrastructure.domain.company.db.persistence;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.cafe.storekeeper.helper.constant.ConstantsTableNames;
import com.cafe.storekeeper.helper.enumerated.EDocumentType;
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
public class CompanyEntity extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = 1430828715777440238L;

    @Field("business_name")
    private String businessName;

    @Field("info")
    private ContactCompanyPojo info;

    @Field("document_number")
    private String documentNumber;

    @Field("document_type")
    private EDocumentType documentType;

    @Field("image")
    private String image;

}
