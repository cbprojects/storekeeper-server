package com.cafe.storekeeper.infrastructure.adapter.model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbstractDTO {

	@JsonProperty("_id")
	private String id;

	@JsonProperty("create_user")
	private String createUser;

	@JsonProperty("update_user")
	private String updateUser;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@JsonProperty("create_date")
	private LocalDateTime createDate;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@JsonProperty("update_date")
	private LocalDateTime updateDate;

}
