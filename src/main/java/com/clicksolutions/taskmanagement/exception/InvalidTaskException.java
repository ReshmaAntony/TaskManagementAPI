package com.clicksolutions.taskmanagement.exception;

public class InvalidTaskException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidTaskException(String message) {
        super(message);
    }
}