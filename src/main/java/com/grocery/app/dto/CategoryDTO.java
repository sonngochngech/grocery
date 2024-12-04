package com.grocery.app.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CategoryDTO {

    private Long id;
    private String name;
    private Date createdAt;
    private Date updatedAt;
    private Boolean isDeleted;
}
