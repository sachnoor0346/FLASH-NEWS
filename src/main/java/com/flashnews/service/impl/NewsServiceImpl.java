package com.flashnews.service.impl;

import com.flashnews.dao.NewsArticleDAO;
import com.flashnews.dao.CategoryDAO;
import com.flashnews.dao.LocationDAO;
import com.flashnews.model.NewsArticle;
import com.flashnews.model.Category;
import com.flashnews.model.Location;
import com.flashnews.service.NewsService;
import com.flashnews.external.NewsAPIClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * NewsServiceImpl class implementing NewsService interface
 * Demonstrates service layer pattern and external API integration
 */
public class NewsServiceImpl implements NewsService {
    private static final Logger logger = LoggerFactory.getLogger(NewsServiceImpl.class);
    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 100;
    
    private final NewsArticleDAO newsArticleDAO;
    private final CategoryDAO categoryDAO;
    private final LocationDAO locationDAO;
    private final NewsAPIClient newsAPIClient;
    private final ExecutorService executorService;
    
    public NewsServiceImpl() {
        this.newsArticleDAO = new NewsArticleDAO();
        this.categoryDAO = new CategoryDAO();
        this.locationDAO = new LocationDAO();
        this.newsAPIClient = new NewsAPIClient();
        this.executorService = Executors.newFixedThreadPool(5);
    }
    
    @Override
    public List<NewsArticle> getLatestNews(Integer categoryId, Integer locationId, int limit) {
        logger.info("Fetching latest news - Category: {}, Location: {}, Limit: {}", 
                   categoryId, locationId, limit);
        
        // Validate and adjust limit
        limit = Math.min(Math.max(limit, 1), MAX_LIMIT);
        
        List<NewsArticle> articles = newsArticleDAO.getLatestNews(categoryId, locationId, limit);
        
        // If not enough articles in cache, fetch from News API
        if (articles.size() < limit) {
            logger.info("Insufficient cached articles, fetching from News API");
            fetchAndCacheNewsFromAPI(categoryId, locationId, limit);
            articles = newsArticleDAO.getLatestNews(categoryId, locationId, limit);
        }
        
        return articles;
    }
    
    @Override
    public List<NewsArticle> getTrendingNews(Integer categoryId, Integer locationId, int limit) {
        logger.info("Fetching trending news - Category: {}, Location: {}, Limit: {}", 
                   categoryId, locationId, limit);
        
        limit = Math.min(Math.max(limit, 1), MAX_LIMIT);
        
        List<NewsArticle> articles = newsArticleDAO.getTrendingNews(categoryId, locationId, limit);
        
        // If not enough trending articles, fetch from News API
        if (articles.size() < limit) {
            logger.info("Insufficient trending articles, fetching from News API");
            fetchAndCacheNewsFromAPI(categoryId, locationId, limit);
            articles = newsArticleDAO.getTrendingNews(categoryId, locationId, limit);
        }
        
        return articles;
    }
    
    @Override
    public List<NewsArticle> searchNews(String keyword, Integer categoryId, Integer locationId, int limit) {
        logger.info("Searching news - Keyword: {}, Category: {}, Location: {}, Limit: {}", 
                   keyword, categoryId, locationId, limit);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        limit = Math.min(Math.max(limit, 1), MAX_LIMIT);
        
        return newsArticleDAO.searchNews(keyword.trim(), categoryId, locationId, limit);
    }
    
    @Override
    public NewsArticle getArticleById(int articleId) {
        logger.info("Fetching article by ID: {}", articleId);
        
        NewsArticle article = newsArticleDAO.getArticleById(articleId);
        if (article != null) {
            // Increment view count asynchronously
            CompletableFuture.runAsync(() -> {
                newsArticleDAO.incrementViewCount(articleId);
            }, executorService);
        }
        
        return article;
    }
    
    @Override
    public NewsArticle getArticleByUrl(String url) {
        logger.info("Fetching article by URL: {}", url);
        return newsArticleDAO.getArticleByUrl(url);
    }
    
    @Override
    public NewsArticle saveArticle(NewsArticle article) {
        logger.info("Saving article: {}", article.getTitle());
        return newsArticleDAO.saveArticle(article);
    }
    
    @Override
    public void incrementViewCount(int articleId) {
        logger.debug("Incrementing view count for article: {}", articleId);
        newsArticleDAO.incrementViewCount(articleId);
    }
    
    @Override
    public List<Category> getAllCategories() {
        logger.info("Fetching all categories");
        return categoryDAO.getAllCategories();
    }
    
    @Override
    public List<Location> getAllLocations() {
        logger.info("Fetching all locations");
        return locationDAO.getAllLocations();
    }
    
    @Override
    public int refreshNewsCache(Integer categoryId, Integer locationId) {
        logger.info("Refreshing news cache from News API - Category: {}, Location: {}", categoryId, locationId);
        
        try {
            // Fetch fresh news from API
            int fetchedCount = fetchAndCacheNewsFromAPI(categoryId, locationId, 100);
            
            // Get count of articles after refresh
            var stats = getNewsStatistics();
            int totalArticles = (Integer) stats.getOrDefault("totalArticles", 0);
            
            logger.info("News cache refresh completed. Fetched {} new articles. Total articles: {}", fetchedCount, totalArticles);
            return fetchedCount;
            
        } catch (Exception e) {
            logger.error("Error refreshing news cache", e);
            return 0;
        }
    }
    
    @Override
    public Map<String, Object> getNewsStatistics() {
        logger.info("Fetching news statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Get total articles count
            int totalArticles = newsArticleDAO.getTotalArticlesCount();
            stats.put("totalArticles", totalArticles);
            
            // Get trending articles count
            int trendingArticles = newsArticleDAO.getTrendingArticlesCount();
            stats.put("trendingArticles", trendingArticles);
            
            // Get recent articles count (last 24 hours)
            int recentArticles = newsArticleDAO.getRecentArticlesCount();
            stats.put("recentArticles", recentArticles);
            
            // Get total views
            int totalViews = newsArticleDAO.getTotalViewsCount();
            stats.put("totalViews", totalViews);
            
            // Get categories count
            int categoriesCount = categoryDAO.getCategoriesCount();
            stats.put("categoriesCount", categoriesCount);
            
            // Get locations count
            int locationsCount = locationDAO.getLocationsCount();
            stats.put("locationsCount", locationsCount);
            
        } catch (Exception e) {
            logger.error("Error fetching news statistics", e);
        }
        
        return stats;
    }
    
    @Override
    public void setTrendingStatus(int articleId, boolean trending) {
        logger.info("Setting trending status for article {}: {}", articleId, trending);
        newsArticleDAO.setTrendingStatus(articleId, trending);
    }
    
    @Override
    public List<NewsArticle> getNewsByFilter(String filterType, Integer categoryId, 
                                           Integer locationId, int limit) {
        logger.info("Fetching news by filter - Type: {}, Category: {}, Location: {}, Limit: {}", 
                   filterType, categoryId, locationId, limit);
        
        if (filterType == null || filterType.trim().isEmpty()) {
            filterType = "latest";
        }
        
        switch (filterType.toLowerCase()) {
            case "trending":
                return getTrendingNews(categoryId, locationId, limit);
            case "local":
                return getLatestNews(categoryId, locationId, limit);
            case "latest":
            default:
                return getLatestNews(categoryId, locationId, limit);
        }
    }
    
    /**
     * Fetch and cache news from News API
     */
    private int fetchAndCacheNewsFromAPI(Integer categoryId, Integer locationId, int limit) {
        try {
            // Map category and location to News API parameters
            String apiCategory = mapCategoryToAPICategory(categoryId);
            String countryCode = mapLocationToCountryCode(locationId);
            
            // Fetch news from API
            List<NewsArticle> apiArticles = newsAPIClient.fetchNews(apiCategory, countryCode, limit);
            
            if (apiArticles.isEmpty()) {
                logger.warn("No articles fetched from News API");
                return 0;
            }
            
            // Get category and location objects for mapping
            Category category = categoryId != null ? categoryDAO.getCategoryById(categoryId) : null;
            Location location = locationId != null ? locationDAO.getLocationById(locationId) : null;
            
            // Save articles to database with proper category and location mapping
            int savedCount = 0;
            for (NewsArticle article : apiArticles) {
                try {
                    // Check if article already exists
                    NewsArticle existing = newsArticleDAO.getArticleByUrl(article.getUrl());
                    if (existing == null) {
                        // Set category and location if provided
                        if (category != null) {
                            article.setCategoryId(category.getId());
                        } else if (apiCategory != null) {
                            // Try to find matching category
                            Category matchedCategory = categoryDAO.getCategoryByName(apiCategory);
                            if (matchedCategory != null) {
                                article.setCategoryId(matchedCategory.getId());
                            }
                        }
                        
                        if (location != null) {
                            article.setLocationId(location.getId());
                        } else if (countryCode != null) {
                            // Try to find matching location by country code
                            Location matchedLocation = locationDAO.getLocationByCountryCode(countryCode);
                            if (matchedLocation != null) {
                                article.setLocationId(matchedLocation.getId());
                            }
                        }
                        
                        // Mark some articles as trending (top 30% by default)
                        if (savedCount < limit * 0.3) {
                            article.setTrending(true);
                        }
                        
                        NewsArticle saved = newsArticleDAO.saveArticle(article);
                        if (saved != null) {
                            savedCount++;
                        }
                    }
                } catch (Exception e) {
                    logger.debug("Error saving article from API: {}", article.getTitle(), e);
                }
            }
            
            logger.info("Successfully cached {} new articles from News API", savedCount);
            return savedCount;
            
        } catch (Exception e) {
            logger.error("Error fetching news from API", e);
            return 0;
        }
    }
    
    /**
     * Map category ID to News API category name
     */
    private String mapCategoryToAPICategory(Integer categoryId) {
        if (categoryId == null) {
            return null;
        }
        
        Category category = categoryDAO.getCategoryById(categoryId);
        if (category == null) {
            return null;
        }
        
        String categoryName = category.getName().toLowerCase();
        
        // Map database categories to News API categories
        // News API supports: business, entertainment, general, health, science, sports, technology
        switch (categoryName) {
            case "technology":
                return "technology";
            case "sports":
                return "sports";
            case "business":
                return "business";
            case "health":
                return "health";
            case "entertainment":
                return "entertainment";
            case "science":
                return "science";
            case "politics":
            case "world":
            default:
                return null; // Use /everything endpoint for these
        }
    }
    
    /**
     * Map location ID to News API country code (ISO 3166-1 alpha-2)
     */
    private String mapLocationToCountryCode(Integer locationId) {
        if (locationId == null) {
            return null;
        }
        
        Location location = locationDAO.getLocationById(locationId);
        if (location == null || location.getCountryCode() == null) {
            return null;
        }
        
        String countryCode = location.getCountryCode();
        
        // Convert ISO 3166-1 alpha-3 to alpha-2 if needed
        // Common mappings
        if (countryCode.length() == 3) {
            switch (countryCode.toUpperCase()) {
                case "USA": return "us";
                case "GBR": return "gb";
                case "CAN": return "ca";
                case "AUS": return "au";
                case "IND": return "in";
                case "DEU": return "de";
                case "FRA": return "fr";
                case "JPN": return "jp";
                case "BRA": return "br";
                case "CHN": return "cn";
                default:
                    // Try to use first two letters if it's a 3-letter code
                    return countryCode.substring(0, 2).toLowerCase();
            }
        } else if (countryCode.length() == 2) {
            return countryCode.toLowerCase();
        }
        
        return null;
    }
    
    /**
     * Shutdown executor service
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            logger.info("News service executor shutdown");
        }
    }
}
