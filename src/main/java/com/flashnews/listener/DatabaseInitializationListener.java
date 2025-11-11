package com.flashnews.listener;

import com.flashnews.database.DatabaseConnection;
import com.flashnews.service.NewsService;
import com.flashnews.service.impl.NewsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

/**
 * DatabaseInitializationListener for initializing database on application startup
 * Demonstrates application lifecycle management and database setup
 */
@WebListener
public class DatabaseInitializationListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializationListener.class);
    private NewsService newsService;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("FlashNews application starting up...");
        
        try {
            // Initialize database connection
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            dbConnection.initialize();
            
            // Test database connection
            if (dbConnection.testConnection()) {
                logger.info("Database connection established successfully");
                
                // Initialize database schema if needed
                initializeDatabaseSchema();
                
                // Initialize news service
                newsService = new NewsServiceImpl();
                
                // Pre-populate with some news if database is empty
                preloadNewsData();
                
                // Store news service in servlet context
                sce.getServletContext().setAttribute("newsService", newsService);
                
                logger.info("FlashNews application initialized successfully");
            } else {
                logger.error("Failed to establish database connection");
            }
            
        } catch (Exception e) {
            logger.error("Error during application initialization", e);
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("FlashNews application shutting down...");
        
        try {
            // Shutdown news service
            if (newsService instanceof NewsServiceImpl) {
                ((NewsServiceImpl) newsService).shutdown();
            }
            
            // Close database connections
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            dbConnection.closeAllConnections();
            
            logger.info("FlashNews application shutdown completed");
            
        } catch (Exception e) {
            logger.error("Error during application shutdown", e);
        }
    }
    
    /**
     * Initialize database schema
     */
    private void initializeDatabaseSchema() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database/schema.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            
            if (input == null) {
                logger.warn("Database schema file not found, skipping initialization");
                return;
            }
            
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            try (Connection conn = dbConnection.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                StringBuilder sqlScript = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    // Skip comments and empty lines
                    if (line.trim().isEmpty() || line.trim().startsWith("--")) {
                        continue;
                    }
                    
                    sqlScript.append(line).append("\n");
                    
                    // Execute when we hit a semicolon
                    if (line.trim().endsWith(";")) {
                        try {
                            stmt.execute(sqlScript.toString());
                            logger.debug("Executed SQL: {}", sqlScript.toString().trim());
                        } catch (Exception e) {
                            // Ignore errors for existing tables/constraints
                            logger.debug("SQL execution warning (likely existing object): {}", e.getMessage());
                        }
                        sqlScript.setLength(0);
                    }
                }
                
                logger.info("Database schema initialization completed");
                
            } finally {
                dbConnection.returnConnection(null);
            }
            
        } catch (Exception e) {
            logger.error("Error initializing database schema", e);
        }
    }
    
    /**
     * Preload some news data if database is empty
     */
    private void preloadNewsData() {
        try {
            // Check if we have any articles
            var stats = newsService.getNewsStatistics();
            int totalArticles = (Integer) stats.getOrDefault("totalArticles", 0);
            
            if (totalArticles == 0) {
                logger.info("No articles found, fetching today's news from News API...");
                
                // Fetch real news from News API
                int fetchedCount = newsService.refreshNewsCache(null, null);
                logger.info("Fetched {} articles from News API", fetchedCount);
            } else {
                logger.info("Database already contains {} articles", totalArticles);
            }
            
        } catch (Exception e) {
            logger.error("Error preloading news data", e);
        }
    }
}
