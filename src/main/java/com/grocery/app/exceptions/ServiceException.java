package com.grocery.app.exceptions;

public class ServiceException extends  RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String code;

    public ServiceException() {
        this.code="UNKNOWN";
    }
    public ServiceException(String message,String code) {
        super(message);
        this.code=code;
    }
}
