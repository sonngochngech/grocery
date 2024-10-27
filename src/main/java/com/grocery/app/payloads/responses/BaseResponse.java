package com.grocery.app.payloads.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {
    @JsonProperty(value="Resultmessage")
    private String message;

    @JsonProperty(value="Resultcode")
    private String code;


}
