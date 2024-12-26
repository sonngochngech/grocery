package com.grocery.app.dto;

import com.grocery.app.entities.Role;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailDTO {
    private  Long  id;

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

    private String avatar;

    private  String password;


    private Role role;

    private Set<DeviceDTO> devices=new HashSet<>();

    private Boolean isActivated;
}
