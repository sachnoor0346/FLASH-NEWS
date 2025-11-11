-- FlashNews Database Schema
-- This script creates the database structure for the FlashNews application

CREATE DATABASE IF NOT EXISTS flashnews_db;
USE flashnews_db;

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

-- Create indexes for better performance
CREATE INDEX idx_news_published_category ON news_articles(published_at DESC, category_id);
CREATE INDEX idx_news_trending_published ON news_articles(is_trending, published_at DESC);
CREATE INDEX idx_reading_history_user_time ON reading_history(user_id, read_at DESC);
