 package com.orderservice.application.exception;

public class InavalidArgumentException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public InavalidArgumentException(String message) {
		super(message);

	}

	public InavalidArgumentException(String message, Throwable t) {
		super(message, t);

	}

}
