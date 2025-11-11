-- Clear existing data and restart with fresh mock data
USE flashnews_db;

-- Clear all existing articles
DELETE FROM news_articles;
DELETE FROM reading_history;
DELETE FROM search_history;

-- Reset auto increment
ALTER TABLE news_articles AUTO_INCREMENT = 1;
ALTER TABLE reading_history AUTO_INCREMENT = 1;
ALTER TABLE search_history AUTO_INCREMENT = 1;

-- Verify tables are empty
SELECT 'Articles cleared' as status, COUNT(*) as count FROM news_articles;
SELECT 'Reading history cleared' as status, COUNT(*) as count FROM reading_history;
SELECT 'Search history cleared' as status, COUNT(*) as count FROM search_history;
