package com.cafe.storekeeper.infrastructure.domain.company.db.pojo;

import java.io.Serializable;

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
public class ContactCompanyPojo implements Serializable {
    private static final long serialVersionUID = 1430828715777440238L;

    @Field("telephone")
    private String telephone;

    @Field("email")
    private String email;

    @Field("address")
    private String address;

    @Field("city")
    private String city;

}