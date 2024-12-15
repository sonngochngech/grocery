package com.grocery.app.dto.request.updateRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRecipeRequest {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private ArrayList<Long> foods;
}
