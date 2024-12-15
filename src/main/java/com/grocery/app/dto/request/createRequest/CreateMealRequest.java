package com.grocery.app.dto.request.createRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateMealRequest {
    Long userId;
    Long familyId;
    String name;
    String term;
    String date;
    ArrayList<Long> recipeList;
}
