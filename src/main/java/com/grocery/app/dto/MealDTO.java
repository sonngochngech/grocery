package com.grocery.app.dto;

import com.grocery.app.dto.family.FamilyDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MealDTO {
    private Long mealId;
    private UserDTO userDetailDTO;
    private FamilyDTO familyDetailDTO;
    private String name;
    private String term;
    private String date;
    private ArrayList<RecipeDTO> recipeDTOS;
    private Date createdAt;
    private Date updatedAt;
    private String status;
}
