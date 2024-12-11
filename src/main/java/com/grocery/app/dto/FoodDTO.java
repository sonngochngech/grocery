package com.grocery.app.dto;

import lombok.*;

import java.sql.Date;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodDTO {
    private Long id;
    private UserDetailDTO user;
    private String name;
    private String description;
    private CategoryDTO categoryDTO;
    private UnitDTO measureUnitDTO;
    private Date createdAt;
    private Date updatedAt;
}
