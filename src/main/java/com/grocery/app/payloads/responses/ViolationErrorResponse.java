package com.grocery.app.payloads.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
public class ViolationErrorResponse extends  ErrorResponse{
    private Map<String,String> errors;

    public ViolationErrorResponse(String code, String message, Map<String,String> errors) {
        super(code, message);
        this.errors = errors;
    }

}
