package com.cafe.storekeeper.infrastructure.domain.bill.rest.model.request;

import com.cafe.storekeeper.helper.enums.EBillStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString(callSuper = true)
@JsonPropertyOrder({ "id", "status" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusBillRequest {

	@JsonProperty("id")
	private String id;

	@JsonProperty("status")
	private EBillStatus status;

}
