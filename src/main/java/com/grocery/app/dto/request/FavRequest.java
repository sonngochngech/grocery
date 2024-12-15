package com.grocery.app.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavRequest {
    private Long recipeId;

    @Builder.Default
    private int from = 0;
    @Builder.Default
    private int to = 10;
}
