package com.grocery.app.config;

public enum StatusConfig {
    DELETED("deleted"),
    AVAILABLE("available");

    private final String status;

    // Constructor
    StatusConfig(String status) {
        this.status = status;
    }

    // Getter
    public String getStatus() {
        return status;
    }
}
