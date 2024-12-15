package com.grocery.app.dto;

import com.grocery.app.dto.family.FamilyDTO;
import com.grocery.app.dto.family.FamilyDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingListDTO {
    private Long id;
    private UserDTO ownerDTO;
    private FamilyDTO familyDTO;
    private String name;
    private String description;
    private ArrayList<TaskDTO> taskArrayList = new ArrayList<>();
    private Date createdAt;
    private Date updatedAt;
    private String status;
}
