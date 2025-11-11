package com.flashnews.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DatabaseConnection class for managing database connections
 * Demonstrates Singleton pattern and connection pooling
 */
public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static DatabaseConnection instance;
    private static final int MAX_POOL_SIZE = 20;
    private static final int INITIAL_POOL_SIZE = 5;
    
    private String driver;
    private String url;
    private String username;
    private String password;
    private BlockingQueue<Connection> connectionPool;
    private boolean initialized = false;
    
    // Private constructor for Singleton pattern
    private DatabaseConnection() {
        loadDatabaseProperties();
    }
    
    /**
     * Get singleton instance of DatabaseConnection
     * @return DatabaseConnection instance
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Load database configuration from properties file
     */
    private void loadDatabaseProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find database.properties file");
            }
            props.load(input);
            
            this.driver = props.getProperty("db.driver");
            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");
            
            logger.info("Database properties loaded successfully");
        } catch (IOException e) {
            logger.error("Error loading database properties", e);
            throw new RuntimeException("Failed to load database properties", e);
        }
    }
    
    /**
     * Initialize connection pool
     */
    public synchronized void initialize() {
        if (initialized) {
            return;
        }
        
        try {
            // Load JDBC driver
            Class.forName(driver);
            
            // Initialize connection pool
            connectionPool = new LinkedBlockingQueue<>(MAX_POOL_SIZE);
            
            // Create initial connections
            for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
                Connection conn = createConnection();
                if (conn != null) {
                    connectionPool.offer(conn);
                }
            }
            
            initialized = true;
            logger.info("Database connection pool initialized with {} connections", INITIAL_POOL_SIZE);
            
        } catch (ClassNotFoundException e) {
            logger.error("JDBC driver not found", e);
            throw new RuntimeException("JDBC driver not found", e);
        } catch (SQLException e) {
            logger.error("Error initializing connection pool", e);
            throw new RuntimeException("Failed to initialize connection pool", e);
        }
    }
    
    /**
     * Create a new database connection
     * @return Database connection
     * @throws SQLException if connection fails
     */
    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
    
    /**
     * Get a connection from the pool
     * @return Database connection
     * @throws SQLException if no connection available
     */
    public Connection getConnection() throws SQLException {
        if (!initialized) {
            initialize();
        }
        
        try {
            Connection conn = connectionPool.poll(10, TimeUnit.SECONDS);
            if (conn == null || conn.isClosed()) {
                conn = createConnection();
            }
            return conn;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted while waiting for connection", e);
        }
    }
    
    /**
     * Return a connection to the pool
     * @param connection Connection to return
     */
    public void returnConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed() && connectionPool.size() < MAX_POOL_SIZE) {
                    connectionPool.offer(connection);
                } else {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.warn("Error returning connection to pool", e);
            }
        }
    }
    
    /**
     * Close all connections in the pool
     */
    public synchronized void closeAllConnections() {
        if (connectionPool != null) {
            Connection conn;
            while ((conn = connectionPool.poll()) != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.warn("Error closing connection", e);
                }
            }
            initialized = false;
            logger.info("All database connections closed");
        }
    }
    
    /**
     * Test database connection
     * @return True if connection successful
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            logger.error("Database connection test failed", e);
            return false;
        }
    }
    
    /**
     * Get connection pool status
     * @return Map containing pool statistics
     */
    public java.util.Map<String, Object> getPoolStatus() {
        java.util.Map<String, Object> status = new java.util.HashMap<>();
        status.put("initialized", initialized);
        status.put("poolSize", connectionPool != null ? connectionPool.size() : 0);
        status.put("maxPoolSize", MAX_POOL_SIZE);
        return status;
    }
}
