package com.grocery.app.dto;

import com.grocery.app.dto.family.FamilyDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MealDTO {
    private Long mealId;
    private UserDetailDTO userDetailDTO;
    private FamilyDetailDTO familyDetailDTO;
    private String term;
    private String date;
    private ArrayList<RecipeDTO> recipeDTOS;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String status;
}
