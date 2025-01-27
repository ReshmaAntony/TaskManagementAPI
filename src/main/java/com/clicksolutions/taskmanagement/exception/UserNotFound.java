package com.clicksolutions.taskmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserNotFound extends RuntimeException{
	
	public UserNotFound(String string) {
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;
	
	/*
	 * private String message;
	 * 
	 * public UserNotFound(String message) { super(message); this.message = message;
	 * }
	 */

}
