package com.grocery.app.dto.request.updateRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTaskRequest {
    private Long taskId;
    private Long shoppingListId;
    private Long userId;
    private Long foodId;
    private float quantity;
}
