package com.grocery.app.exceptions;

import com.grocery.app.config.constant.ResCode;
import lombok.Getter;

@Getter
public class ResourceException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    public static final String NOT_FOUND= ResCode.RESOURCE_NOT_FOUND.getCode();
    public static final String EXISTED= ResCode.RESOURCE_EXISTED.getCode();

    private String resourceName;
    private String field;
    private String fieldName;
    private String fieldId;
    private String code;




    public ResourceException() {
    }

    public ResourceException(String code,String resourceName, String field, String fieldName) {
        super(getMessage(code, resourceName, field, fieldName));
        this.code=code;
    }
    public ResourceException(String code,String resourceName, String field, Long fieldId) {
        super(getMessage(code, resourceName, field, fieldId));
        this.code=code;
    }

    private static String getMessage(String code, String resourceName, String field, String fieldName) {
        if (code.equals(NOT_FOUND)) {
            return String.format("%s không tìm thấy với %s: %s", resourceName, field, fieldName);
        } else if (code.equals(EXISTED)) {
            return String.format("%s đã tồn tại với %s: %s", resourceName, field, fieldName);
        } else {
            return "Unknown error code";
        }
    }

    private static String getMessage(String code, String resourceName, String field, Long fieldId) {
        if (code.equals(NOT_FOUND)) {
            return String.format("%s không tìm thấy với %s: %s", resourceName, field, fieldId);
        } else if (code.equals(EXISTED)) {
            return String.format("%s đã tồn tại với %s: %s", resourceName, field, fieldId);
        } else {
            return "Unknown error code";
        }
    }




}
