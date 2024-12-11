package com.grocery.app.dto;

import com.grocery.app.config.constant.StatusConfig;
import lombok.*;

import java.sql.Date;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodDTO {
    private Long id;
    private UserDTO user;
    private String name;
    private String description;
    private CategoryDTO categoryDTO;
    private UnitDTO measureUnitDTO;
    private Date createdAt;
    private Date updatedAt;
    private String status = StatusConfig.AVAILABLE.getStatus();
}
