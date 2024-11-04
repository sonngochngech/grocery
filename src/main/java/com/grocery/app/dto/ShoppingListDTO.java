package com.grocery.app.dto;

import java.sql.Date;
import java.util.ArrayList;

public class ShoppingListDTO {
    private long id;
    private long ownerId;
    private long familyId;
    private String name;
    private String description;
    private ArrayList<TaskDTO> taskArrayList = new ArrayList<>();
    private Date createdAt;
    private Date updatedAt;
    private String status;

    // Default constructor
    public ShoppingListDTO() {}

    // Getters and Setters
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

    public long getFamilyId() {
        return familyId;
    }

    public void setFamilyId(long familyId) {
        this.familyId = familyId;
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

    public ArrayList<TaskDTO> getTaskArrayList() {
        return taskArrayList;
    }

    public void setTaskArrayList(ArrayList<TaskDTO> taskArrayList) {
        this.taskArrayList = taskArrayList;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
