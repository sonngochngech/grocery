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

//    Auth Code
    GET_VERIFY_CODE_SUCCESSFULLY("00001", "Get verify code successfully"),
    INVALID_VERIFY_CODE("00002", "Invalid verify code"),
    WRONG_VERIFY_CODE("00003", "Wrong verify code"),
    VERIFY_CODE_SUCCESSFULLY("00004", "Verify code successfully"),

//    User code
    REGISTER_SUCCESSFULLY("00101", "Register successfully"),
    LOGIN_SUCCESSFULLY("00102", "Login successfully"),
    WRONG_LOGIN_CREDENTIAL("00103", "Wrong login credential"),
    INVALID_REFRESH_TOKEN("00104", "Invalid refresh token"),
    GET_ACCESS_TOKEN_SUCCESSFULLY("00105", "Get access token error"),
    ROLE_NOT_FOUND("00106", "Role not found"),
    USERNAME_NOT_FOUND("00106", "Username not found"),
    INACTIVATED_ACCOUNT("00107", "Inactivated account"),
    GET_USER_SUCCESSFULLY("00108", "Get user successfully"),
    UPDATE_USER_SUCCESSFULLY("00109", "Update user successfully"),
    LOCK_USER_SUCCESSFULLY("00110", "Lock user successfully"),
    USER_NOT_FOUND("00111", "User not found"),
    EXISTED_USER("00112", "Existed user"),

//    Family Code
    CREATE_FAMILY_SUCCESSFULLY("00201", "Create family successfully"),
    GET_FAMILIES_SUCCESSFULLY("00202", "Get families successfully"),
    DELETE_FAMILY_SUCCESSFULLY("00203", "Delete family successfully"),
    DELETE_FAMILY_MEMBER_SUCCESSFULLY("00204", "Delete family member successfully"),
    GET_FAMILY_SUCCESSFULLY("00205", "Get family successfully"),
    NOT_BELONG_TO_FAMILY("00206", "Not belong to family"),
    NOT_OWNER_OF_FAMILY("00207", "Not owner of family"),
    FAMILY_NOT_FOUND("00208", "Family not found"),
    NOT_REMOVED_OWNER("00209", "Not removed owner"),

//    Category Code
    NO_CATEGORY("00301", "No category"),
    CATEGORY_EXIST("00302", "Category existed"),
    CATEGORY_NOT_FOUND("00303", "Category not found"),
    GET_CATEGORY_SUCCESSFULLY("00304", "Get category successfully"),
    CREATE_CATEGORY_SUCCESSFULLY("00305", "Create category successfully"),
    UPDATE_CATEGORY_SUCCESSFULLY("00306", "Update category successfully"),
    DELETE_CATEGORY_SUCCESSFULLY("00307", "Delete category successfully"),

//    Unit Code
    NO_UNIT("00401", "No unit"),
    UNIT_EXIST("00402", "Unit existed"),
    UNIT_NOT_FOUND("00403", "Unit not found"),
    GET_UNITS_SUCCESSFULLY("00404", "Get unit successfully"),
    CREATE_UNIT_SUCCESSFULLY("00405", "Create unit successfully"),
    UPDATE_UNIT_SUCCESSFULLY("00406", "Update unit successfully"),
    DELETE_UNIT_SUCCESSFULLY("00407", "Delete unit successfully"),


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
