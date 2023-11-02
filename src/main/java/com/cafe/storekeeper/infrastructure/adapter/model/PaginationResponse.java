package com.cafe.storekeeper.infrastructure.adapter.model;

import java.io.Serializable;

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
public class PaginationResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonProperty("record_count")
	private long recordCount;

	@JsonProperty("total_count")
	private long totalCount;
}
