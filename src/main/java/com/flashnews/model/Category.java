package com.flashnews.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Category model class representing news categories
 * Demonstrates encapsulation and data validation
 */
public class Category {
    private int id;
    private String name;
    private String displayName;
    private String description;
    private boolean isActive;
    private LocalDateTime createdAt;
    
    // Default constructor
    public Category() {}
    
    // Constructor with essential fields
    public Category(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }
    
    // Full constructor
    public Category(int id, String name, String displayName, String description, 
                   boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        this.name = name.toLowerCase().trim();
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("Display name cannot be null or empty");
        }
        this.displayName = displayName.trim();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Business methods
    public void activate() {
        this.isActive = true;
    }
    
    public void deactivate() {
        this.isActive = false;
    }
    
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() && 
               displayName != null && !displayName.trim().isEmpty();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id && Objects.equals(name, category.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
    
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
