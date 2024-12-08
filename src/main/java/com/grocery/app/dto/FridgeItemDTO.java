package com.grocery.app.dto;


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
    

}
