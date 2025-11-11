package com.flashnews.service;

import com.flashnews.model.NewsArticle;
import com.flashnews.model.Category;
import com.flashnews.model.Location;
import java.util.List;
import java.util.Map;

/**
 * NewsService interface defining contract for news-related operations
 * Demonstrates interface usage and abstraction
 */
public interface NewsService {
    
    /**
     * Fetch latest news articles
     * @param categoryId Category ID filter (optional)
     * @param locationId Location ID filter (optional)
     * @param limit Maximum number of articles to return
     * @return List of news articles
     */
    List<NewsArticle> getLatestNews(Integer categoryId, Integer locationId, int limit);
    
    /**
     * Fetch trending news articles
     * @param categoryId Category ID filter (optional)
     * @param locationId Location ID filter (optional)
     * @param limit Maximum number of articles to return
     * @return List of trending news articles
     */
    List<NewsArticle> getTrendingNews(Integer categoryId, Integer locationId, int limit);
    
    /**
     * Search news articles by keyword
     * @param keyword Search keyword
     * @param categoryId Category ID filter (optional)
     * @param locationId Location ID filter (optional)
     * @param limit Maximum number of articles to return
     * @return List of matching news articles
     */
    List<NewsArticle> searchNews(String keyword, Integer categoryId, Integer locationId, int limit);
    
    /**
     * Get news article by ID
     * @param articleId Article ID
     * @return News article or null if not found
     */
    NewsArticle getArticleById(int articleId);
    
    /**
     * Get news article by URL
     * @param url Article URL
     * @return News article or null if not found
     */
    NewsArticle getArticleByUrl(String url);
    
    /**
     * Save or update news article
     * @param article News article to save
     * @return Saved news article with ID
     */
    NewsArticle saveArticle(NewsArticle article);
    
    /**
     * Update article view count
     * @param articleId Article ID
     */
    void incrementViewCount(int articleId);
    
    /**
     * Get all available categories
     * @return List of active categories
     */
    List<Category> getAllCategories();
    
    /**
     * Get all available locations
     * @return List of active locations
     */
    List<Location> getAllLocations();
    
    /**
     * Refresh news cache from external API
     * @param categoryId Category ID filter (optional)
     * @param locationId Location ID filter (optional)
     * @return Number of articles cached
     */
    int refreshNewsCache(Integer categoryId, Integer locationId);
    
    /**
     * Get news statistics
     * @return Map containing various news statistics
     */
    Map<String, Object> getNewsStatistics();
    
    /**
     * Mark article as trending
     * @param articleId Article ID
     * @param trending Trending status
     */
    void setTrendingStatus(int articleId, boolean trending);
    
    /**
     * Get articles by filter type
     * @param filterType Filter type (latest, trending, local)
     * @param categoryId Category ID filter (optional)
     * @param locationId Location ID filter (optional)
     * @param limit Maximum number of articles to return
     * @return List of filtered news articles
     */
    List<NewsArticle> getNewsByFilter(String filterType, Integer categoryId, Integer locationId, int limit);
}
