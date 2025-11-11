-- FlashNews Quick MySQL Setup
-- Run this for a quick database setup

-- 1. Create database
DROP DATABASE IF EXISTS flashnews_db;
CREATE DATABASE flashnews_db;
USE flashnews_db;

-- 2. Create essential tables
CREATE TABLE categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE locations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE NOT NULL,
    country_code VARCHAR(3) NOT NULL,
    timezone VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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
    FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE SET NULL
);

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

-- 3. Insert default data
INSERT INTO categories (name, display_name, description) VALUES
('technology', 'Technology', 'Latest technology news and innovations'),
('sports', 'Sports', 'Sports news and updates'),
('business', 'Business', 'Business and financial news'),
('health', 'Health', 'Health and medical news'),
('entertainment', 'Entertainment', 'Entertainment and celebrity news'),
('science', 'Science', 'Scientific discoveries and research'),
('politics', 'Politics', 'Political news and analysis'),
('world', 'World', 'International news and events');

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

-- 4. Create indexes
CREATE INDEX idx_news_published ON news_articles(published_at DESC);
CREATE INDEX idx_news_trending ON news_articles(is_trending);
CREATE INDEX idx_news_category ON news_articles(category_id);
CREATE INDEX idx_news_location ON news_articles(location_id);

-- 5. Verify setup
SELECT 'Database setup completed!' as status;
SELECT COUNT(*) as categories_count FROM categories;
SELECT COUNT(*) as locations_count FROM locations;
