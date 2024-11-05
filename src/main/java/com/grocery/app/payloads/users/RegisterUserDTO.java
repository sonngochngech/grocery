package com.grocery.app.payloads.users;

import com.grocery.app.dto.DeviceDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDTO {
    @NotNull(message = "First name is required")
    @Size(min = 3, max = 50, message = "First name must be between 3 and 50 characters")
    private String  firstName;

    @Size(min = 3, max = 50, message = "Last name must be between 3 and 50 characters")
    @NotNull(message = "Last name is required")
    private  String  lastName;

    @NotNull(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;


    private  String  phoneNumber;

    private  String  email;

    @NotNull(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private  String password;

    private Date birthday;

    private String sexType;

    private DeviceDTO deviceDTO;

    private String verifyCode;
}
