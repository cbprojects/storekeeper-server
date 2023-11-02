package com.cafe.storekeeper.infrastructure.adapter.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
@JsonPropertyOrder({ "result", "_pagination" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardPaginationRestResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonProperty("result")
	private Object result;

	@JsonProperty("_pagination")
	private PaginationResponse pagination;

	private void writeObject(ObjectOutputStream oos)
			throws IOException {
		oos.defaultWriteObject();
		oos.writeObject(result);
	}

	private void readObject(ObjectInputStream ois)
			throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		this.setResult(ois.readObject());
	}

}
