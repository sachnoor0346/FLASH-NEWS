-- FlashNews MySQL Database Setup Script
-- Run this script to create the complete database structure

-- =============================================
-- 1. CREATE DATABASE
-- =============================================
DROP DATABASE IF EXISTS flashnews_db;
CREATE DATABASE flashnews_db;
USE flashnews_db;

-- =============================================
-- 2. CREATE TABLES
-- =============================================

-- Users table for authentication and personalization
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    is_admin BOOLEAN DEFAULT FALSE
);

-- Categories table for news categories
CREATE TABLE categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Locations table for area-based news filtering
CREATE TABLE locations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE NOT NULL,
    country_code VARCHAR(3) NOT NULL,
    timezone VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User preferences table
CREATE TABLE user_preferences (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    preferred_category_id INT,
    preferred_location_id INT,
    news_filter ENUM('latest', 'trending', 'local') DEFAULT 'latest',
    auto_refresh_interval INT DEFAULT 300, -- seconds
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (preferred_category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (preferred_location_id) REFERENCES locations(id) ON DELETE SET NULL
);

-- News articles cache table
CREATE TABLE news_articles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    content TEXT,
    url VARCHAR(1000) NOT NULL,
    image_url VARCHAR(1000),
    source_name VARCHAR(100),
    source_url VARCHAR(500),
    category_id INT,
    location_id INT,
    published_at TIMESTAMP,
    cached_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_trending BOOLEAN DEFAULT FALSE,
    view_count INT DEFAULT 0,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE SET NULL,
    INDEX idx_published_at (published_at),
    INDEX idx_category_location (category_id, location_id),
    INDEX idx_trending (is_trending)
);

-- User reading history
CREATE TABLE reading_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    article_id INT NOT NULL,
    read_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    time_spent INT DEFAULT 0, -- seconds
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (article_id) REFERENCES news_articles(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_article (user_id, article_id)
);

-- Search history for analytics
CREATE TABLE search_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    search_query VARCHAR(255) NOT NULL,
    results_count INT DEFAULT 0,
    searched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- =============================================
-- 3. INSERT DEFAULT DATA
-- =============================================

-- Insert default categories
INSERT INTO categories (name, display_name, description) VALUES
('technology', 'Technology', 'Latest technology news and innovations'),
('sports', 'Sports', 'Sports news and updates'),
('business', 'Business', 'Business and financial news'),
('health', 'Health', 'Health and medical news'),
('entertainment', 'Entertainment', 'Entertainment and celebrity news'),
('science', 'Science', 'Scientific discoveries and research'),
('politics', 'Politics', 'Political news and analysis'),
('world', 'World', 'International news and events');

-- Insert default locations
INSERT INTO locations (name, country_code, timezone) VALUES
('United States', 'USA', 'America/New_York'),
('United Kingdom', 'GBR', 'Europe/London'),
('Canada', 'CAN', 'America/Toronto'),
('Australia', 'AUS', 'Australia/Sydney'),
('India', 'IND', 'Asia/Kolkata'),
('Germany', 'DEU', 'Europe/Berlin'),
('France', 'FRA', 'Europe/Paris'),
('Japan', 'JPN', 'Asia/Tokyo'),
('Brazil', 'BRA', 'America/Sao_Paulo'),
('China', 'CHN', 'Asia/Shanghai');

-- Insert sample admin user (password: admin123)
INSERT INTO users (username, email, password_hash, full_name, is_admin) VALUES
('admin', 'admin@flashnews.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Administrator', TRUE);

-- Insert sample regular user (password: user123)
INSERT INTO users (username, email, password_hash, full_name, is_admin) VALUES
('user', 'user@flashnews.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Regular User', FALSE);

-- =============================================
-- 4. CREATE INDEXES FOR PERFORMANCE
-- =============================================

-- News articles indexes
CREATE INDEX idx_news_published_category ON news_articles(published_at DESC, category_id);
CREATE INDEX idx_news_trending_published ON news_articles(is_trending, published_at DESC);
CREATE INDEX idx_news_url ON news_articles(url);
CREATE INDEX idx_news_source ON news_articles(source_name);

-- Reading history indexes
CREATE INDEX idx_reading_history_user_time ON reading_history(user_id, read_at DESC);
CREATE INDEX idx_reading_history_article ON reading_history(article_id);

-- Search history indexes
CREATE INDEX idx_search_history_user ON search_history(user_id);
CREATE INDEX idx_search_history_time ON search_history(searched_at DESC);
CREATE INDEX idx_search_history_query ON search_history(search_query);

-- User preferences indexes
CREATE INDEX idx_user_preferences_user ON user_preferences(user_id);

-- =============================================
-- 5. CREATE VIEWS FOR COMMON QUERIES
-- =============================================

-- View for active categories with article counts
CREATE VIEW v_active_categories AS
SELECT 
    c.id,
    c.name,
    c.display_name,
    c.description,
    COUNT(na.id) as article_count
FROM categories c
LEFT JOIN news_articles na ON c.id = na.category_id
WHERE c.is_active = TRUE
GROUP BY c.id, c.name, c.display_name, c.description;

-- View for active locations with article counts
CREATE VIEW v_active_locations AS
SELECT 
    l.id,
    l.name,
    l.country_code,
    l.timezone,
    COUNT(na.id) as article_count
FROM locations l
LEFT JOIN news_articles na ON l.id = na.location_id
WHERE l.is_active = TRUE
GROUP BY l.id, l.name, l.country_code, l.timezone;

-- View for trending articles
CREATE VIEW v_trending_articles AS
SELECT 
    na.*,
    c.display_name as category_name,
    l.name as location_name
FROM news_articles na
LEFT JOIN categories c ON na.category_id = c.id
LEFT JOIN locations l ON na.location_id = l.id
WHERE na.is_trending = TRUE
ORDER BY na.published_at DESC;

-- View for recent articles (last 24 hours)
CREATE VIEW v_recent_articles AS
SELECT 
    na.*,
    c.display_name as category_name,
    l.name as location_name
FROM news_articles na
LEFT JOIN categories c ON na.category_id = c.id
LEFT JOIN locations l ON na.location_id = l.id
WHERE na.published_at >= DATE_SUB(NOW(), INTERVAL 24 HOUR)
ORDER BY na.published_at DESC;

-- =============================================
-- 6. CREATE STORED PROCEDURES
-- =============================================

DELIMITER //

-- Procedure to get news statistics
CREATE PROCEDURE GetNewsStatistics()
BEGIN
    SELECT 
        (SELECT COUNT(*) FROM news_articles) as total_articles,
        (SELECT COUNT(*) FROM news_articles WHERE is_trending = TRUE) as trending_articles,
        (SELECT COUNT(*) FROM news_articles WHERE published_at >= DATE_SUB(NOW(), INTERVAL 24 HOUR)) as recent_articles,
        (SELECT SUM(view_count) FROM news_articles) as total_views,
        (SELECT COUNT(*) FROM categories WHERE is_active = TRUE) as categories_count,
        (SELECT COUNT(*) FROM locations WHERE is_active = TRUE) as locations_count;
END //

-- Procedure to refresh news cache
CREATE PROCEDURE RefreshNewsCache(
    IN p_category_id INT,
    IN p_location_id INT
)
BEGIN
    -- This procedure would be called by the application
    -- to refresh news cache from external API
    SELECT 'News cache refresh initiated' as message;
END //

-- Procedure to get user reading history
CREATE PROCEDURE GetUserReadingHistory(
    IN p_user_id INT,
    IN p_limit INT
)
BEGIN
    SELECT 
        rh.*,
        na.title,
        na.url,
        na.source_name,
        c.display_name as category_name
    FROM reading_history rh
    JOIN news_articles na ON rh.article_id = na.id
    LEFT JOIN categories c ON na.category_id = c.id
    WHERE rh.user_id = p_user_id
    ORDER BY rh.read_at DESC
    LIMIT p_limit;
END //

DELIMITER ;

-- =============================================
-- 7. CREATE TRIGGERS
-- =============================================

-- Trigger to update article view count
DELIMITER //
CREATE TRIGGER tr_update_view_count
AFTER INSERT ON reading_history
FOR EACH ROW
BEGIN
    UPDATE news_articles 
    SET view_count = view_count + 1 
    WHERE id = NEW.article_id;
END //
DELIMITER ;

-- Trigger to set trending status based on view count
DELIMITER //
CREATE TRIGGER tr_set_trending_status
AFTER UPDATE ON news_articles
FOR EACH ROW
BEGIN
    IF NEW.view_count > 100 AND OLD.view_count <= 100 THEN
        UPDATE news_articles 
        SET is_trending = TRUE 
        WHERE id = NEW.id;
    END IF;
END //
DELIMITER ;

-- =============================================
-- 8. GRANT PERMISSIONS (Optional)
-- =============================================

-- Create a dedicated user for the application
-- CREATE USER 'flashnews_user'@'localhost' IDENTIFIED BY 'flashnews_password';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON flashnews_db.* TO 'flashnews_user'@'localhost';
-- FLUSH PRIVILEGES;

-- =============================================
-- 9. VERIFY SETUP
-- =============================================

-- Check if all tables were created
SELECT 
    TABLE_NAME,
    TABLE_ROWS,
    CREATE_TIME
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'flashnews_db'
ORDER BY TABLE_NAME;

-- Check if default data was inserted
SELECT 'Categories' as table_name, COUNT(*) as record_count FROM categories
UNION ALL
SELECT 'Locations', COUNT(*) FROM locations
UNION ALL
SELECT 'Users', COUNT(*) FROM users;

-- Check indexes
SHOW INDEX FROM news_articles;

-- =============================================
-- 10. SAMPLE QUERIES FOR TESTING
-- =============================================

-- Get all active categories
SELECT * FROM v_active_categories;

-- Get all active locations
SELECT * FROM v_active_locations;

-- Get trending articles
SELECT * FROM v_trending_articles LIMIT 10;

-- Get recent articles
SELECT * FROM v_recent_articles LIMIT 10;

-- Get news statistics
CALL GetNewsStatistics();

-- =============================================
-- SETUP COMPLETE
-- =============================================

SELECT 'FlashNews database setup completed successfully!' as status;
SELECT 'You can now run your Java application' as next_step;
