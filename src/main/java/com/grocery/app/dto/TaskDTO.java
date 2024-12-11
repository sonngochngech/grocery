package com.grocery.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDTO {
    private Long id;
    private ShoppingListDTO shoppingListDTO;
    private UserDetailDTO user;
    private FoodDTO foodDTO;
    private float quantity;
    private Date createdAt;
    private Date updatedAt;
    private String status;
}

