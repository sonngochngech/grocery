package com.grocery.app.payloads.users;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetVerifyCodeDTO {
    @NotNull(message = "Email is required")
    private String email;
}
