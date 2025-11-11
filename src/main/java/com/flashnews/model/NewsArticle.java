package com.flashnews.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * NewsArticle model class representing a news article
 * Demonstrates encapsulation with private fields and public getters/setters
 */
public class NewsArticle {
    private int id;
    private String title;
    private String description;
    private String content;
    private String url;
    private String imageUrl;
    private String sourceName;
    private String sourceUrl;
    private int categoryId;
    private int locationId;
    private LocalDateTime publishedAt;
    private LocalDateTime cachedAt;
    private boolean isTrending;
    private int viewCount;
    
    // Default constructor
    public NewsArticle() {}
    
    // Constructor with essential fields
    public NewsArticle(String title, String description, String url, String sourceName) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.sourceName = sourceName;
        this.publishedAt = LocalDateTime.now();
        this.cachedAt = LocalDateTime.now();
        this.viewCount = 0;
        this.isTrending = false;
    }
    
    // Full constructor
    public NewsArticle(int id, String title, String description, String content, String url, 
                      String imageUrl, String sourceName, String sourceUrl, int categoryId, 
                      int locationId, LocalDateTime publishedAt, LocalDateTime cachedAt, 
                      boolean isTrending, int viewCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.content = content;
        this.url = url;
        this.imageUrl = imageUrl;
        this.sourceName = sourceName;
        this.sourceUrl = sourceUrl;
        this.categoryId = categoryId;
        this.locationId = locationId;
        this.publishedAt = publishedAt;
        this.cachedAt = cachedAt;
        this.isTrending = isTrending;
        this.viewCount = viewCount;
    }
    
    // Getters and Setters (Encapsulation)
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getSourceName() {
        return sourceName;
    }
    
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    
    public String getSourceUrl() {
        return sourceUrl;
    }
    
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public int getLocationId() {
        return locationId;
    }
    
    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }
    
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
    
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
    
    public LocalDateTime getCachedAt() {
        return cachedAt;
    }
    
    public void setCachedAt(LocalDateTime cachedAt) {
        this.cachedAt = cachedAt;
    }
    
    public boolean isTrending() {
        return isTrending;
    }
    
    public void setTrending(boolean trending) {
        isTrending = trending;
    }
    
    public int getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
    
    // Business methods
    public void incrementViewCount() {
        this.viewCount++;
    }
    
    public boolean isRecent() {
        return publishedAt.isAfter(LocalDateTime.now().minusHours(24));
    }
    
    public String getShortDescription() {
        if (description == null || description.length() <= 150) {
            return description;
        }
        return description.substring(0, 147) + "...";
    }
    
    // Override equals and hashCode for proper object comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsArticle that = (NewsArticle) o;
        return id == that.id && Objects.equals(url, that.url);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, url);
    }
    
    @Override
    public String toString() {
        return "NewsArticle{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", publishedAt=" + publishedAt +
                '}';
    }
}
