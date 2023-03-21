package com.cafe.storekeeper.infrastructure.adapter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@JsonPropertyOrder({ "result", "_pagination" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardPaginationRestResponse {

	@JsonProperty("result")
	private Object result;

	@JsonProperty("_pagination")
	private PaginationResponse pagination;

}
