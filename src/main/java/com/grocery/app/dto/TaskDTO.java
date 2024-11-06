package com.grocery.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String status;
}

