package com.grocery.app.dto.request.createRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTaskRequest {
    private Long shoppingListId;
    private Long assignee;
    private Long foodId;
    private float quantity;
    private Timestamp timestamp;
}
