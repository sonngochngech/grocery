package com.grocery.app.dto.family;

import com.grocery.app.dto.UserDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FamilyDTO {

    private Long id;
    private String name;

    private UserDTO owner;
    private Date createdAt;
    private Date updatedAt;
    private Boolean isDeleted;
}
