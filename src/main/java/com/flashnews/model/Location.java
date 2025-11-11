package com.flashnews.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Location model class representing geographical locations for news filtering
 * Demonstrates encapsulation and validation
 */
public class Location {
    private int id;
    private String name;
    private String countryCode;
    private String timezone;
    private boolean isActive;
    private LocalDateTime createdAt;
    
    // Default constructor
    public Location() {}
    
    // Constructor with essential fields
    public Location(String name, String countryCode) {
        this.name = name;
        this.countryCode = countryCode;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }
    
    // Full constructor
    public Location(int id, String name, String countryCode, String timezone, 
                   boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.countryCode = countryCode;
        this.timezone = timezone;
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
            throw new IllegalArgumentException("Location name cannot be null or empty");
        }
        this.name = name.trim();
    }
    
    public String getCountryCode() {
        return countryCode;
    }
    
    public void setCountryCode(String countryCode) {
        if (countryCode == null || countryCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Country code cannot be null or empty");
        }
        this.countryCode = countryCode.toUpperCase().trim();
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
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
               countryCode != null && !countryCode.trim().isEmpty();
    }
    
    public String getDisplayName() {
        return name + " (" + countryCode + ")";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return id == location.id && Objects.equals(name, location.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
    
    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
