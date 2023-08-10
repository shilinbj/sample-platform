package com.klaus.saas.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.klaus.saas.commons.enums.StatusCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author shilin
 * @since 2019-09-16
 */
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class DataResult<T> extends Result {

	protected T item;

	public DataResult(T item) {
		super(StatusCode.OK);
		this.item = item;
	}

	public DataResult(StatusCode statusCode) {
		super(statusCode);
	}

	public DataResult(T item, StatusCode statusCode) {
		super(statusCode);
		this.item = item;
	}

	public DataResult(int code, String message, T item) {
		super(code, message);
		this.item = item;
	}

}
