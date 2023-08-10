package com.klaus.saas.commons.model;

import com.klaus.saas.commons.enums.StatusCode;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shilin
 * @since 2019-09-16
 */
@Data
@NoArgsConstructor
public class Result {
	protected int code;
	protected String message;

	public Result(StatusCode statusCode) {
		this.code = statusCode.getCode();
		this.message = statusCode.getMessage();
	}

	public Result(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public static Result OK() {
		return new Result(StatusCode.OK);
	}

}
