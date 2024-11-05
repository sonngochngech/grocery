package com.grocery.app.exceptions;

public class ControllerException extends  ServiceException {
    private static final long serialVersionUID = 1L;

    public ControllerException(String message,String code) {
        super(message,code);
    }
}
