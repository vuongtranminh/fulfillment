package com.bluebelt.fulfillment.exception;

import com.bluebelt.fulfillment.payload.response.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AppException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;
	private ExceptionResponse exceptionResponse;

	public AppException(String message) {
		super(message);
		this.message = message;
	}

	public AppException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExceptionResponse getExceptionResponse() {
		List<String> messages = new ArrayList<>(1);
		messages.add(message);
		return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(), messages);
	}
}
