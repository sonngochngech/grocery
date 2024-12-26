package com.grocery.app.dto.request.createRequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTaskRequest {
    private Long shoppingListId;
    private Long assignee;
    private Long foodId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    private LocalDateTime dueDateTime = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
    private float quantity;
}
