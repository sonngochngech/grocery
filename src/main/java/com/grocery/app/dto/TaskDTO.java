package com.grocery.app.dto;

import com.grocery.app.entities.Food;
import com.grocery.app.entities.MeasureUnit;

public class TaskDTO {
    private long id;
    private long buyerId;
    private FoodDTO foodDTO;
    private MeasureUnitDTO measureUnitDTO;
    private float quantity;
    public TaskDTO(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(long buyerId) {
        this.buyerId = buyerId;
    }

    public FoodDTO getFood() {
        return foodDTO;
    }

    public void setFood(FoodDTO food) {
        this.foodDTO = food;
    }

    public MeasureUnitDTO getMeasureUnit() {
        return measureUnitDTO;
    }

    public void setMeasureUnit(MeasureUnitDTO measureUnit) {
        this.measureUnitDTO = measureUnit;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }
}

