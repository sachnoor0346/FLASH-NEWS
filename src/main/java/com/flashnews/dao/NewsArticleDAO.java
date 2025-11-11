package com.flashnews.dao;

import com.flashnews.database.DatabaseConnection;
import com.flashnews.model.NewsArticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * NewsArticleDAO class for database operations on news articles
 * Demonstrates DAO pattern and SQL operations
 */
public class NewsArticleDAO {
    private static final Logger logger = LoggerFactory.getLogger(NewsArticleDAO.class);
    private final DatabaseConnection dbConnection;
    
    public NewsArticleDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Get latest news articles with optional filters
     */
    public List<NewsArticle> getLatestNews(Integer categoryId, Integer locationId, int limit) {
        List<NewsArticle> articles = new ArrayList<>();
        String sql = buildNewsQuery("latest", categoryId, locationId, limit);
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setQueryParameters(stmt, categoryId, locationId, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    articles.add(mapResultSetToArticle(rs));
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error fetching latest news", e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return articles;
    }
    
    /**
     * Get trending news articles with optional filters
     */
    public List<NewsArticle> getTrendingNews(Integer categoryId, Integer locationId, int limit) {
        List<NewsArticle> articles = new ArrayList<>();
        String sql = buildNewsQuery("trending", categoryId, locationId, limit);
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setQueryParameters(stmt, categoryId, locationId, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    articles.add(mapResultSetToArticle(rs));
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error fetching trending news", e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return articles;
    }
    
    /**
     * Search news articles by keyword
     */
    public List<NewsArticle> searchNews(String keyword, Integer categoryId, Integer locationId, int limit) {
        List<NewsArticle> articles = new ArrayList<>();
        String sql = buildSearchQuery(categoryId, locationId, limit);
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            setQueryParameters(stmt, categoryId, locationId, limit, 3);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    articles.add(mapResultSetToArticle(rs));
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error searching news", e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return articles;
    }
    
    /**
     * Get news article by ID
     */
    public NewsArticle getArticleById(int articleId) {
        String sql = "SELECT * FROM news_articles WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, articleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToArticle(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error fetching article by ID: " + articleId, e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return null;
    }
    
    /**
     * Get news article by URL
     */
    public NewsArticle getArticleByUrl(String url) {
        String sql = "SELECT * FROM news_articles WHERE url = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, url);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToArticle(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error fetching article by URL: " + url, e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return null;
    }
    
    /**
     * Save or update news article
     */
    public NewsArticle saveArticle(NewsArticle article) {
        if (article.getId() > 0) {
            return updateArticle(article);
        } else {
            return insertArticle(article);
        }
    }
    
    /**
     * Insert new news article
     */
    private NewsArticle insertArticle(NewsArticle article) {
        String sql = "INSERT INTO news_articles (title, description, content, url, image_url, " +
                    "source_name, source_url, category_id, location_id, published_at, cached_at, " +
                    "is_trending, view_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, article.getTitle());
            stmt.setString(2, article.getDescription());
            stmt.setString(3, article.getContent());
            stmt.setString(4, article.getUrl());
            stmt.setString(5, article.getImageUrl());
            stmt.setString(6, article.getSourceName());
            stmt.setString(7, article.getSourceUrl());
            stmt.setObject(8, article.getCategoryId() > 0 ? article.getCategoryId() : null);
            stmt.setObject(9, article.getLocationId() > 0 ? article.getLocationId() : null);
            stmt.setTimestamp(10, article.getPublishedAt() != null ? 
                Timestamp.valueOf(article.getPublishedAt()) : Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(11, article.getCachedAt() != null ? 
                Timestamp.valueOf(article.getCachedAt()) : Timestamp.valueOf(LocalDateTime.now()));
            stmt.setBoolean(12, article.isTrending());
            stmt.setInt(13, article.getViewCount());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        article.setId(generatedKeys.getInt(1));
                    }
                }
                logger.info("Article inserted with ID: " + article.getId());
                return article;
            }
            
        } catch (SQLException e) {
            logger.error("Error inserting article", e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return null;
    }
    
    /**
     * Update existing news article
     */
    private NewsArticle updateArticle(NewsArticle article) {
        String sql = "UPDATE news_articles SET title = ?, description = ?, content = ?, " +
                    "url = ?, image_url = ?, source_name = ?, source_url = ?, category_id = ?, " +
                    "location_id = ?, published_at = ?, is_trending = ?, view_count = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, article.getTitle());
            stmt.setString(2, article.getDescription());
            stmt.setString(3, article.getContent());
            stmt.setString(4, article.getUrl());
            stmt.setString(5, article.getImageUrl());
            stmt.setString(6, article.getSourceName());
            stmt.setString(7, article.getSourceUrl());
            stmt.setObject(8, article.getCategoryId() > 0 ? article.getCategoryId() : null);
            stmt.setObject(9, article.getLocationId() > 0 ? article.getLocationId() : null);
            stmt.setTimestamp(10, article.getPublishedAt() != null ? 
                Timestamp.valueOf(article.getPublishedAt()) : null);
            stmt.setBoolean(11, article.isTrending());
            stmt.setInt(12, article.getViewCount());
            stmt.setInt(13, article.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Article updated with ID: " + article.getId());
                return article;
            }
            
        } catch (SQLException e) {
            logger.error("Error updating article", e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return null;
    }
    
    /**
     * Increment article view count
     */
    public boolean incrementViewCount(int articleId) {
        String sql = "UPDATE news_articles SET view_count = view_count + 1 WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, articleId);
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
            
        } catch (SQLException e) {
            logger.error("Error incrementing view count for article: " + articleId, e);
            return false;
        } finally {
            dbConnection.returnConnection(null);
        }
    }
    
    /**
     * Set trending status for article
     */
    public boolean setTrendingStatus(int articleId, boolean trending) {
        String sql = "UPDATE news_articles SET is_trending = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, trending);
            stmt.setInt(2, articleId);
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
            
        } catch (SQLException e) {
            logger.error("Error setting trending status for article: " + articleId, e);
            return false;
        } finally {
            dbConnection.returnConnection(null);
        }
    }
    
    /**
     * Build news query based on filter type
     */
    private String buildNewsQuery(String filterType, Integer categoryId, Integer locationId, int limit) {
        StringBuilder sql = new StringBuilder("SELECT * FROM news_articles WHERE 1=1");
        
        if ("trending".equals(filterType)) {
            sql.append(" AND is_trending = true");
        }
        
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND category_id = ?");
        }
        
        if (locationId != null && locationId > 0) {
            sql.append(" AND location_id = ?");
        }
        
        sql.append(" ORDER BY published_at DESC LIMIT ?");
        
        return sql.toString();
    }
    
    /**
     * Build search query
     */
    private String buildSearchQuery(Integer categoryId, Integer locationId, int limit) {
        StringBuilder sql = new StringBuilder("SELECT * FROM news_articles WHERE " +
                "(title LIKE ? OR description LIKE ?)");
        
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND category_id = ?");
        }
        
        if (locationId != null && locationId > 0) {
            sql.append(" AND location_id = ?");
        }
        
        sql.append(" ORDER BY published_at DESC LIMIT ?");
        
        return sql.toString();
    }
    
    /**
     * Set query parameters
     */
    private void setQueryParameters(PreparedStatement stmt, Integer categoryId, 
                                  Integer locationId, int limit) throws SQLException {
        setQueryParameters(stmt, categoryId, locationId, limit, 1);
    }
    
    /**
     * Set query parameters with offset
     */
    private void setQueryParameters(PreparedStatement stmt, Integer categoryId, 
                                  Integer locationId, int limit, int paramIndex) throws SQLException {
        int currentIndex = paramIndex;
        
        if (categoryId != null && categoryId > 0) {
            stmt.setInt(currentIndex++, categoryId);
        }
        
        if (locationId != null && locationId > 0) {
            stmt.setInt(currentIndex++, locationId);
        }
        
        stmt.setInt(currentIndex, limit);
    }
    
    /**
     * Get total articles count
     */
    public int getTotalArticlesCount() {
        String sql = "SELECT COUNT(*) FROM news_articles";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            logger.error("Error getting total articles count", e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return 0;
    }
    
    /**
     * Get trending articles count
     */
    public int getTrendingArticlesCount() {
        String sql = "SELECT COUNT(*) FROM news_articles WHERE is_trending = true";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            logger.error("Error getting trending articles count", e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return 0;
    }
    
    /**
     * Get recent articles count (last 24 hours)
     */
    public int getRecentArticlesCount() {
        String sql = "SELECT COUNT(*) FROM news_articles WHERE published_at >= DATE_SUB(NOW(), INTERVAL 24 HOUR)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            logger.error("Error getting recent articles count", e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return 0;
    }
    
    /**
     * Get total views count
     */
    public int getTotalViewsCount() {
        String sql = "SELECT SUM(view_count) FROM news_articles";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            logger.error("Error getting total views count", e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return 0;
    }
    
    /**
     * Map ResultSet to NewsArticle object
     */
    private NewsArticle mapResultSetToArticle(ResultSet rs) throws SQLException {
        NewsArticle article = new NewsArticle();
        article.setId(rs.getInt("id"));
        article.setTitle(rs.getString("title"));
        article.setDescription(rs.getString("description"));
        article.setContent(rs.getString("content"));
        article.setUrl(rs.getString("url"));
        article.setImageUrl(rs.getString("image_url"));
        article.setSourceName(rs.getString("source_name"));
        article.setSourceUrl(rs.getString("source_url"));
        article.setCategoryId(rs.getInt("category_id"));
        article.setLocationId(rs.getInt("location_id"));
        
        Timestamp publishedAt = rs.getTimestamp("published_at");
        if (publishedAt != null) {
            article.setPublishedAt(publishedAt.toLocalDateTime());
        }
        
        Timestamp cachedAt = rs.getTimestamp("cached_at");
        if (cachedAt != null) {
            article.setCachedAt(cachedAt.toLocalDateTime());
        }
        
        article.setTrending(rs.getBoolean("is_trending"));
        article.setViewCount(rs.getInt("view_count"));
        
        return article;
    }
}
