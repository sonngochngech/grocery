package com.grocery.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDTO {
    private Long id;
    private Long shoppingListId;
    private UserDTO assignee;
    private FoodDTO foodDTO;
    private float quantity;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    private LocalDateTime dueDateTime = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
    private Date createdAt;
    private Date updatedAt;
    private String status;
}

