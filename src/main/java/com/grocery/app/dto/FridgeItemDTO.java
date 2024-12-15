package com.grocery.app.dto;


import com.grocery.app.config.constant.ResCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FridgeItemDTO {
    private Long id;
    private Integer duration;
    private Integer quantity;
    private Date  createdAt;
    private Date updatedAt;
    private FoodFridgeDTO food;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FoodFridgeDTO {
        private Long id;
        private String name;
        private String description;
        private String status;
    }
}
