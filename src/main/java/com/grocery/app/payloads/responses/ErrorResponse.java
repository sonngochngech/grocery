package com.grocery.app.payloads.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorResponse  extends  BaseResponse<Void>{
    private String code;
    private String message;



}
