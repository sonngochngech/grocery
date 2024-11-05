package com.grocery.app.payloads.responses;

import com.grocery.app.dto.UserDetailDTO;
import com.grocery.app.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
@AllArgsConstructor
public class AuthResponse extends  BaseResponse<UserDetailDTO> {
    private String accessToken;
    private String refreshToken;
    public AuthResponse(String message, String code, UserDetailDTO user, String accessToken, String refreshToken) {
        super(message, code,user);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
