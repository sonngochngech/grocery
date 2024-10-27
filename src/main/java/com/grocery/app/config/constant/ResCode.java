package com.grocery.app.config.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ResCode {
//    Common Error
    VALIDATION_ERROR("0001", "Validation error"),
    AUTHENTICATION_ERROR("0002", "Authentication error"),
    INTERNAl_SERVER_ERROR("0003", "Internal server error"),
    RESOURCE_EXISTED("0004", "Resource existed"),
    RESOURCE_NOT_FOUND("0005", "Resource not found"),
    REDIS_ERROR("0006", "Redis error"),
    VALIDATION_ERROR_FIELD("0007", "Validation error field"),

//    User code
    REGISTER_SUCCESSFULLY("0008", "Register successfully"),
    LOGIN_SUCCESSFULLY("0009", "Login successfully"),
    WRONG_LOGIN_CREDENTIAL("0010", "Wrong login credential"),
    INVALID_REFRESH_TOKEN("0011", "Invalid refresh token"),
    GET_ACCESS_TOKEN_SUCCESSFULLY("0012", "Get access token error"),
    ROLE_NOT_FOUND("0013", "Role not found"),
    USERNAME_NOT_FOUND("0014", "Username not found"),
    INACTIVATED_ACCOUNT("00015", "Inactivated account"),

    ;

    private final String code;
    private final String message;

    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }




}
