package com.grocery.app.utils;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.exceptions.ServiceException;

public class Validate {

    private Validate() throws ServiceException {
        throw new ServiceException(ResCode.VALIDATION_ERROR.getCode(), ResCode.VALIDATION_ERROR.getMessage());
    }
    public static void state(boolean expression, String code, String message) throws ServiceException {
        if (!expression) {
            throw new ServiceException(code, message);
        }
    }
    public static  void stateNot(boolean expression, String code, String message) throws ServiceException {
        if (expression) {
            throw new ServiceException(code, message);
        }
    }
}
