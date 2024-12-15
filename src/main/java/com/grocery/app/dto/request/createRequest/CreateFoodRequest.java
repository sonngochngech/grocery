package com.grocery.app.dto.request.createRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateFoodRequest {
    private Long unitId;
    private Long categoryId;
    private String name;
    private String description;
}
