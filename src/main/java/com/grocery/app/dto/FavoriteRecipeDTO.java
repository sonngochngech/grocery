package com.grocery.app.dto;

import com.grocery.app.dto.family.FamilyDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteRecipeDTO {
    private Long Id;
    private FamilyDetailDTO familyDetailDTO;
    private UserDetailDTO userDetailDTO;
    private List<RecipeDTO> favoriteDTOList;
}
