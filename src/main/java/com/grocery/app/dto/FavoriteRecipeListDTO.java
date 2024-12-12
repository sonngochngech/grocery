package com.grocery.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteRecipeListDTO {
    private Long Id;
    private Long userId;

    //
    private List<RecipeDTO> favoriteDTOList;
}
