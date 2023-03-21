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
@JsonPropertyOrder({ "record_count", "total_count" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationResponse {

	@JsonProperty("record_count")
	private long recordCount;

	@JsonProperty("total_count")
	private long totalCount;
}
