package com.cafe.storekeeper.infrastructure.domain.client.rest.model.dto;

import java.io.Serializable;

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
public class ContactPersonDTO implements Serializable {
    private static final long serialVersionUID = 1430828715777440238L;

    @JsonProperty("telephone")
    private String telephone;

    @JsonProperty("email")
    private String email;

    @JsonProperty("address")
    private String address;

    @JsonProperty("city")
    private String city;

}