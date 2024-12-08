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
    FILE_NOT_FOUND("0008", "File NOT FOUND"),
    FILE_EXCEED_SIZE("0009", "File exceed size"),
    FILE_NOT_IMAGE("0010", "File not image"),
    FILE_UPLOAD_ERROR("0011", "File upload error"),

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
    UPLOAD_AVATAR_SUCCESSFULLY("00113", "Upload avatar successfully"),

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
    ALREADY_IN_FAMILY("00210", "Already in family"),

//    Category Code
    NO_CATEGORY("00301", "No category"),
    CATEGORY_EXIST("00302", "Category existed"),
    CATEGORY_NOT_FOUND("00303", "Category not found"),
    GET_CATEGORY_SUCCESSFULLY("00304", "Get category successfully"),
    CREATE_CATEGORY_SUCCESSFULLY("00305", "Create category successfully"),
    UPDATE_CATEGORY_SUCCESSFULLY("00306", "Update category successfully"),
    DELETE_CATEGORY_SUCCESSFULLY("00307", "Delete category successfully"),

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
//    Unit Code
    NO_UNIT("00401", "No unit"),
    UNIT_EXIST("00402", "Unit existed"),
    UNIT_NOT_FOUND("00403", "Unit not found"),
    GET_UNITS_SUCCESSFULLY("00404", "Get unit successfully"),
    CREATE_UNIT_SUCCESSFULLY("00405", "Create unit successfully"),
    UPDATE_UNIT_SUCCESSFULLY("00406", "Update unit successfully"),
    DELETE_UNIT_SUCCESSFULLY("00407", "Delete unit successfully"),

//    Fridge Code
    GET_FRIDGE_SUCCESSFULLY("00501", "Get fridge successfully"),
    ADD_ITEM_TO_FRIDGE_SUCCESSFULLY("00502", "Add item to fridge successfully"),
    REMOVE_ITEM_FROM_FRIDGE_SUCCESSFULLY("00503", "Remove item from fridge successfully"),
    UPDATE_ITEM_IN_FRIDGE_SUCCESSFULLY("00504", "Update item in fridge successfully"),
    FRIDGE_NOT_FOUND("00505", "Fridge not found"),
    ITEM_NOT_FOUND("00506", "Item not found"),

// Invitation Code
    INVITATION_SUCCESSFULLY("00601", "Invitation successfully"),
    RESPONSE_INVITATION_SUCCESSFULLY("00602", "Response invitation successfully"),
    INVITATION_NOT_FOUND("00603", "Invitation not found"),
    NOT_BELONG_TO_INVITATION("00604", "Not belong to invitation"),
    INVITATION_ALREADY_ACCEPTED("00605", "Invitation already accepted"),
    INVITATION_ALREADY_REJECTED("00606", "Invitation already rejected"),
    INVITATION_EXPIRED("00607", "Invitation expired"),
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
