package com.grocery.app.payloads.responses;

import com.grocery.app.dto.UserDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
@AllArgsConstructor
public class AuthResponse extends  BaseResponse {
    private String accessToken;
    private String refreshToken;
    private UserDetailDTO user;
    public AuthResponse(String message, String code, UserDetailDTO user, String accessToken, String refreshToken) {
        super(code, message);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

}
