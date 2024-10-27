package com.grocery.app.payloads.loginCredentials;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsCredentials {

    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "^(\\+84|0)\\d{9,10}$", message = "Invalid phone number")
    private String phoneNumber;

    @Pattern(regexp = "^\\d{6}$", message = "Invalid verify code")
    private String verifyCode;
}
