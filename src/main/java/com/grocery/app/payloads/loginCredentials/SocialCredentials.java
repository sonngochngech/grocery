package com.grocery.app.payloads.loginCredentials;

import static com.grocery.app.config.constant.AppConstants.AuthProviderType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialCredentials {

    @NotNull(message = "Token id is required")
    private String tokenId;

    @NotNull(message = "Provider is required")
    private AuthProviderType provider;

}
