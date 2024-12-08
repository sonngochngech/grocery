package com.grocery.app.dto.request.updateRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateFoodRequest {
    private Long id;
    private Long userId;
    private Long unitId;
    private Long categoryId;
    private String name;
    private String description;
}
