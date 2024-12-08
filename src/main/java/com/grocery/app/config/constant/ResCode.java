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

// Food code
    FOOD_NOT_FOUND("00301", "Food is not found"),
    FOOD_CREATION_FAILED("00402", "Create food fail"),
    FOOD_UPDATE_FAILED("00403", "Update food fail"),
    FOOD_DELETE_FAILED("00404", "Delete food fail"),
    NOT_FOOD_OWNER("00405", "User doesn't own the food"),

// Shopping list code
    SHOPPING_LIST_NOT_FOUND("00401", "Shopping list is not found"),
    SHOPPING_LIST_CREATION_FAILED("00402", "Create shopping list fail"),
    SHOPPING_LIST_NOT_DELETED("00403", "Shopping list is not deleted"),
    NOT_SHOPPING_LIST_OWNER("00404", "User is not shopping list owner"),
// Task code
    TASK_NOT_FOUND("00501", "Task is not found"),
    TASK_NOT_IN_SHOPPING_LIST("00502", "Task is not in shopping list"),
    TASK_NOT_DELETED("00503", "Task is not deleted"),

// Recipe code
    RECIPE_NOT_FOUND("00601", "Recipe is not found"),

// Meal code
    MEAL_NOT_FOUND("00701", "Meal is not found"),
    MEAL_CREATION_FAILED("00702", "Create meal fail"),
    MEAL_UPDATE_FAILED("00703", "Update meal fail"),
    MEAL_DELETE_FAILED("00704", "Delete meal fail"),
    NOT_MEAL_OWNER("00705", "User doesn't own the meal plan"),

// Enum code
    INVALID_TERM("99901", "Invalid term"),
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
