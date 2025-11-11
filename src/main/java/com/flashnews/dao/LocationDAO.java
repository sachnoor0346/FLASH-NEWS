package com.flashnews.dao;

import com.flashnews.database.DatabaseConnection;
import com.flashnews.model.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * LocationDAO class for database operations on locations
 * Demonstrates DAO pattern for location management
 */
public class LocationDAO {
    private static final Logger logger = LoggerFactory.getLogger(LocationDAO.class);
    private final DatabaseConnection dbConnection;
    
    public LocationDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Get all active locations
     */
    public List<Location> getAllLocations() {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT * FROM locations WHERE is_active = true ORDER BY name";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                locations.add(mapResultSetToLocation(rs));
            }
            
        } catch (SQLException e) {
            logger.error("Error fetching all locations", e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return locations;
    }
    
    /**
     * Get location by ID
     */
    public Location getLocationById(int locationId) {
        String sql = "SELECT * FROM locations WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, locationId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLocation(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error fetching location by ID: " + locationId, e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return null;
    }
    
    /**
     * Get location by country code
     */
    public Location getLocationByCountryCode(String countryCode) {
        String sql = "SELECT * FROM locations WHERE country_code = ? AND is_active = true";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, countryCode.toUpperCase().trim());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLocation(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error fetching location by country code: " + countryCode, e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return null;
    }
    
    /**
     * Save or update location
     */
    public Location saveLocation(Location location) {
        if (location.getId() > 0) {
            return updateLocation(location);
        } else {
            return insertLocation(location);
        }
    }
    
    /**
     * Insert new location
     */
    private Location insertLocation(Location location) {
        String sql = "INSERT INTO locations (name, country_code, timezone, is_active, created_at) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, location.getName());
            stmt.setString(2, location.getCountryCode());
            stmt.setString(3, location.getTimezone());
            stmt.setBoolean(4, location.isActive());
            stmt.setTimestamp(5, Timestamp.valueOf(location.getCreatedAt() != null ? 
                location.getCreatedAt() : LocalDateTime.now()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        location.setId(generatedKeys.getInt(1));
                    }
                }
                logger.info("Location inserted with ID: " + location.getId());
                return location;
            }
            
        } catch (SQLException e) {
            logger.error("Error inserting location", e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return null;
    }
    
    /**
     * Update existing location
     */
    private Location updateLocation(Location location) {
        String sql = "UPDATE locations SET name = ?, country_code = ?, timezone = ?, " +
                    "is_active = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, location.getName());
            stmt.setString(2, location.getCountryCode());
            stmt.setString(3, location.getTimezone());
            stmt.setBoolean(4, location.isActive());
            stmt.setInt(5, location.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Location updated with ID: " + location.getId());
                return location;
            }
            
        } catch (SQLException e) {
            logger.error("Error updating location", e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return null;
    }
    
    /**
     * Delete location (soft delete)
     */
    public boolean deleteLocation(int locationId) {
        String sql = "UPDATE locations SET is_active = false WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, locationId);
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
            
        } catch (SQLException e) {
            logger.error("Error deleting location: " + locationId, e);
            return false;
        } finally {
            dbConnection.returnConnection(null);
        }
    }
    
    /**
     * Get locations count
     */
    public int getLocationsCount() {
        String sql = "SELECT COUNT(*) FROM locations WHERE is_active = true";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            logger.error("Error getting locations count", e);
        } finally {
            dbConnection.returnConnection(null);
        }
        
        return 0;
    }
    
    /**
     * Map ResultSet to Location object
     */
    private Location mapResultSetToLocation(ResultSet rs) throws SQLException {
        Location location = new Location();
        location.setId(rs.getInt("id"));
        location.setName(rs.getString("name"));
        location.setCountryCode(rs.getString("country_code"));
        location.setTimezone(rs.getString("timezone"));
        location.setActive(rs.getBoolean("is_active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            location.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return location;
    }
}
