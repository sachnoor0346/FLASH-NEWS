package com.flashnews.service;

import com.flashnews.model.User;
import com.flashnews.model.Category;
import com.flashnews.model.Location;
import java.util.List;
import java.util.Map;

/**
 * UserService interface defining contract for user-related operations
 * Demonstrates interface usage and user management abstraction
 */
public interface UserService {
    
    /**
     * Authenticate user with username and password
     * @param username Username
     * @param password Plain text password
     * @return User object if authentication successful, null otherwise
     */
    User authenticateUser(String username, String password);
    
    /**
     * Register a new user
     * @param username Username
     * @param email Email address
     * @param password Plain text password
     * @param fullName Full name (optional)
     * @return Created user object
     */
    User registerUser(String username, String email, String password, String fullName);
    
    /**
     * Get user by ID
     * @param userId User ID
     * @return User object or null if not found
     */
    User getUserById(int userId);
    
    /**
     * Get user by username
     * @param username Username
     * @return User object or null if not found
     */
    User getUserByUsername(String username);
    
    /**
     * Get user by email
     * @param email Email address
     * @return User object or null if not found
     */
    User getUserByEmail(String email);
    
    /**
     * Update user information
     * @param user User object with updated information
     * @return Updated user object
     */
    User updateUser(User user);
    
    /**
     * Update user password
     * @param userId User ID
     * @param newPassword New plain text password
     * @return True if password updated successfully
     */
    boolean updatePassword(int userId, String newPassword);
    
    /**
     * Delete user account
     * @param userId User ID
     * @return True if user deleted successfully
     */
    boolean deleteUser(int userId);
    
    /**
     * Update user preferences
     * @param userId User ID
     * @param preferredCategoryId Preferred category ID
     * @param preferredLocationId Preferred location ID
     * @param newsFilter News filter preference
     * @param autoRefreshInterval Auto-refresh interval in seconds
     * @return True if preferences updated successfully
     */
    boolean updateUserPreferences(int userId, Integer preferredCategoryId, 
                                 Integer preferredLocationId, String newsFilter, 
                                 Integer autoRefreshInterval);
    
    /**
     * Get user preferences
     * @param userId User ID
     * @return Map containing user preferences
     */
    Map<String, Object> getUserPreferences(int userId);
    
    /**
     * Add article to reading history
     * @param userId User ID
     * @param articleId Article ID
     * @param timeSpent Time spent reading in seconds
     * @return True if added successfully
     */
    boolean addToReadingHistory(int userId, int articleId, int timeSpent);
    
    /**
     * Get user reading history
     * @param userId User ID
     * @param limit Maximum number of entries to return
     * @return List of reading history entries
     */
    List<Map<String, Object>> getReadingHistory(int userId, int limit);
    
    /**
     * Add search to search history
     * @param userId User ID (can be null for anonymous users)
     * @param searchQuery Search query
     * @param resultsCount Number of results found
     * @return True if added successfully
     */
    boolean addToSearchHistory(Integer userId, String searchQuery, int resultsCount);
    
    /**
     * Get user search history
     * @param userId User ID
     * @param limit Maximum number of entries to return
     * @return List of search history entries
     */
    List<Map<String, Object>> getSearchHistory(int userId, int limit);
    
    /**
     * Check if username is available
     * @param username Username to check
     * @return True if username is available
     */
    boolean isUsernameAvailable(String username);
    
    /**
     * Check if email is available
     * @param email Email to check
     * @return True if email is available
     */
    boolean isEmailAvailable(String email);
    
    /**
     * Get all users (admin only)
     * @return List of all users
     */
    List<User> getAllUsers();
    
    /**
     * Get user statistics
     * @param userId User ID
     * @return Map containing user statistics
     */
    Map<String, Object> getUserStatistics(int userId);
}
