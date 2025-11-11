package com.flashnews.dao;

import com.flashnews.database.DatabaseConnection;
import com.flashnews.model.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * CategoryDAO class for database operations on categories
 * Demonstrates DAO pattern for category management
 */
public class CategoryDAO {
    private static final Logger logger = LoggerFactory.getLogger(CategoryDAO.class);
    private final DatabaseConnection dbConnection;
    
    public CategoryDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Get all active categories
     */
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE is_active = true ORDER BY display_name";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
            
        } catch (SQLException e) {
            logger.error("Error fetching all categories", e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return categories;
    }
    
    /**
     * Get category by ID
     */
    public Category getCategoryById(int categoryId) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categoryId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategory(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error fetching category by ID: " + categoryId, e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return null;
    }
    
    /**
     * Get category by name
     */
    public Category getCategoryByName(String name) {
        String sql = "SELECT * FROM categories WHERE name = ? AND is_active = true";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name.toLowerCase().trim());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategory(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error fetching category by name: " + name, e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return null;
    }
    
    /**
     * Save or update category
     */
    public Category saveCategory(Category category) {
        if (category.getId() > 0) {
            return updateCategory(category);
        } else {
            return insertCategory(category);
        }
    }
    
    /**
     * Insert new category
     */
    private Category insertCategory(Category category) {
        String sql = "INSERT INTO categories (name, display_name, description, is_active, created_at) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDisplayName());
            stmt.setString(3, category.getDescription());
            stmt.setBoolean(4, category.isActive());
            stmt.setTimestamp(5, Timestamp.valueOf(category.getCreatedAt() != null ? 
                category.getCreatedAt() : LocalDateTime.now()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        category.setId(generatedKeys.getInt(1));
                    }
                }
                logger.info("Category inserted with ID: " + category.getId());
                return category;
            }
            
        } catch (SQLException e) {
            logger.error("Error inserting category", e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return null;
    }
    
    /**
     * Update existing category
     */
    private Category updateCategory(Category category) {
        String sql = "UPDATE categories SET name = ?, display_name = ?, description = ?, " +
                    "is_active = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDisplayName());
            stmt.setString(3, category.getDescription());
            stmt.setBoolean(4, category.isActive());
            stmt.setInt(5, category.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Category updated with ID: " + category.getId());
                return category;
            }
            
        } catch (SQLException e) {
            logger.error("Error updating category", e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return null;
    }
    
    /**
     * Delete category (soft delete)
     */
    public boolean deleteCategory(int categoryId) {
        String sql = "UPDATE categories SET is_active = false WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categoryId);
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
            
        } catch (SQLException e) {
            logger.error("Error deleting category: " + categoryId, e);
            return false;
        } finally {
            dbConnection.returnConnection(null);
        }
    }
    
    /**
     * Get categories count
     */
    public int getCategoriesCount() {
        String sql = "SELECT COUNT(*) FROM categories WHERE is_active = true";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            logger.error("Error getting categories count", e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return 0;
    }
    
    /**
     * Map ResultSet to Category object
     */
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt("id"));
        category.setName(rs.getString("name"));
        category.setDisplayName(rs.getString("display_name"));
        category.setDescription(rs.getString("description"));
        category.setActive(rs.getBoolean("is_active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            category.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return category;
    }
}
