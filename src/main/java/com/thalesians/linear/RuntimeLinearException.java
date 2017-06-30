package com.thalesians.linear;

public class RuntimeLinearException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public RuntimeLinearException() {
		super();
	}
	
	public RuntimeLinearException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public RuntimeLinearException(String message) {
		super(message);
	}
	
	public RuntimeLinearException(Throwable cause) {
		super(cause);
	}
}
