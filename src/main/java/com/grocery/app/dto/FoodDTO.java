package com.grocery.app.dto;

import com.grocery.app.entities.Category;
import com.grocery.app.entities.MeasureUnit;

import java.sql.Date;

public class FoodDTO {
    private long id;
    private long ownerId;
    private String name;
    private String description;
    private CategoryDTO categoryDTO;
    private MeasureUnitDTO measureUnitDTO;
    private Date createdAt;
    private Date updatedAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategoryDTO getCategoryDTO() {
        return categoryDTO;
    }

    public void setCategoryDTO(CategoryDTO categoryDTO) {
        this.categoryDTO = categoryDTO;
    }

    public MeasureUnitDTO getMeasureUnitDTO() {
        return measureUnitDTO;
    }

    public void setMeasureUnitDTO(MeasureUnitDTO measureUnitDTO) {
        this.measureUnitDTO = measureUnitDTO;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
