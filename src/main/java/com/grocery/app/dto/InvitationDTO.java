package com.grocery.app.dto;


import com.grocery.app.dto.family.FamilyDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InvitationDTO {
    private Long id;
    private String name;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private UserDTO user;
    private FamilyDTO family;
}
