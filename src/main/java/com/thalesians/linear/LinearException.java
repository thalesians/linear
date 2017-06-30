package com.thalesians.linear;

public class LinearException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public LinearException() {
		super();
	}
	
	public LinearException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public LinearException(String message) {
		super(message);
	}
	
	public LinearException(Throwable cause) {
		super(cause);
	}
}
