package com.grocery.app.dto.request.createRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRecipeRequest {
    private String name;
    private String description;
    private String imageUrl;
    private ArrayList<Long> foods;
}
