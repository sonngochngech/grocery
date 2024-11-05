package com.grocery.app.payloads.users;

import com.grocery.app.config.constant.AppConstants;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserDTO {

    @Size(min = 3, max = 50, message = "Last name must be between 3 and 50 characters")
    private String firstName;

    @Size(min = 3, max = 50, message = "Last name must be between 3 and 50 characters")
    private  String lastName;


    private Date  birthday;


    private String sexType;
}
