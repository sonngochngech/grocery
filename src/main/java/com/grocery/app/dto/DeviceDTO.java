package com.grocery.app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceDTO {
    private Long id;

    @NotNull(message = "Device ID is required")
    @Size(min = 3, max = 50, message = "Device ID must be between 3 and 50 characters")
    private String deviceId;

    private String deviceType;
}
