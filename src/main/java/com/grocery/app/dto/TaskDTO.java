package com.grocery.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDTO {
    private Long id;
    private Long shoppingListId;
    private UserDTO assignee;
    private FoodDTO foodDTO;
    private Timestamp timestamp;
    private float quantity;
    private Date createdAt;
    private Date updatedAt;
    private String status;
}

