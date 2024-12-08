package com.grocery.app.dto;

import lombok.*;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodDTO {
    private long id;
    private long ownerId;
    private String name;
    private String description;
    private CategoryDTO categoryDTO;
    private UnitDTO measureUnitDTO;
    private Date createdAt;
    private Date updatedAt;
}
