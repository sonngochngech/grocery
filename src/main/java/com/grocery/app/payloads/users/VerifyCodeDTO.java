package com.grocery.app.payloads.users;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyCodeDTO {

    @NotNull(message = "Mail is required")
    private String email;

    @NotNull(message = "Verify code is required")
    private String code;
}
