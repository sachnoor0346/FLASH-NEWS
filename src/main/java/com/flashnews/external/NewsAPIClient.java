package com.flashnews.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flashnews.model.NewsArticle;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * NewsAPIClient class for integrating with external news APIs
 * Demonstrates external API integration and JSON processing
 */
public class NewsAPIClient {
    private static final Logger logger = LoggerFactory.getLogger(NewsAPIClient.class);
    private static final String NEWS_API_BASE_URL = "https://newsapi.org/v2";
    private static final DateTimeFormatter API_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter API_DATE_FORMAT_WITH_Z = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    private final String apiKey;
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public NewsAPIClient() {
        this.apiKey = loadApiKey();
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Load API key from properties file
     */
    private String loadApiKey() {
        Properties props = new Properties();
        try (var input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                props.load(input);
                return props.getProperty("news.api.key", "");
            }
        } catch (IOException e) {
            logger.error("Error loading API key from properties", e);
        }
        return "";
    }
    
    /**
     * Fetch news articles from external API
     */
    public List<NewsArticle> fetchNews(String category, String countryCode, int limit) {
        List<NewsArticle> articles = new ArrayList<>();
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            logger.warn("News API key not configured, returning empty list");
            return articles;
        }
        
        try {
            String url = buildApiUrl(category, countryCode, limit);
            // Mask API key in logs for security
            String maskedUrl = url.replace("apiKey=" + apiKey, "apiKey=***");
            logger.info("Fetching news from API: {}", maskedUrl);
            
            HttpGet request = new HttpGet(url);
            request.setHeader("User-Agent", "FlashNews/1.0");
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                
                if (statusCode == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    articles = parseApiResponse(responseBody);
                    logger.info("Successfully fetched {} articles from API", articles.size());
                } else {
                    logger.error("API request failed with status code: {}", statusCode);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error fetching news from API", e);
        }
        
        return articles;
    }
    
    /**
     * Build API URL with parameters
     */
    private String buildApiUrl(String category, String countryCode, int limit) {
        StringBuilder url = new StringBuilder(NEWS_API_BASE_URL);
        
        if (category != null && !category.trim().isEmpty()) {
            url.append("/top-headlines?category=").append(category);
        } else {
            url.append("/everything?q=news");
        }
        
        if (countryCode != null && !countryCode.trim().isEmpty()) {
            url.append("&country=").append(countryCode.toLowerCase());
        }
        
        url.append("&pageSize=").append(Math.min(limit, 100));
        url.append("&sortBy=publishedAt");
        url.append("&language=en");
        url.append("&apiKey=").append(apiKey);
        
        return url.toString();
    }
    
    /**
     * Parse API response JSON
     */
    private List<NewsArticle> parseApiResponse(String responseBody) {
        List<NewsArticle> articles = new ArrayList<>();
        
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode articlesNode = rootNode.get("articles");
            
            if (articlesNode != null && articlesNode.isArray()) {
                for (JsonNode articleNode : articlesNode) {
                    NewsArticle article = parseArticleNode(articleNode);
                    if (article != null && isValidArticle(article)) {
                        articles.add(article);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error parsing API response", e);
        }
        
        return articles;
    }
    
    /**
     * Parse individual article node
     */
    private NewsArticle parseArticleNode(JsonNode articleNode) {
        try {
            String title = getStringValue(articleNode, "title");
            String description = getStringValue(articleNode, "description");
            String url = getStringValue(articleNode, "url");
            String imageUrl = getStringValue(articleNode, "urlToImage");
            String publishedAt = getStringValue(articleNode, "publishedAt");
            
            if (title == null || url == null) {
                return null;
            }
            
            NewsArticle article = new NewsArticle(title, description, url, 
                getStringValue(articleNode, "source", "name"));
            
            article.setImageUrl(imageUrl);
            article.setSourceUrl(getStringValue(articleNode, "source", "url"));
            article.setContent(getStringValue(articleNode, "content"));
            
            // Parse published date
            if (publishedAt != null && !publishedAt.trim().isEmpty()) {
                try {
                    LocalDateTime publishedDateTime;
                    if (publishedAt.endsWith("Z")) {
                        // Handle format: 2025-10-26T06:52:51Z
                        String cleanDate = publishedAt.replace("Z", "");
                        publishedDateTime = LocalDateTime.parse(cleanDate, API_DATE_FORMAT);
                    } else {
                        // Handle format: 2025-10-26T06:52:51.000Z
                        String cleanDate = publishedAt.replace("Z", "").replaceAll("\\.\\d+", "");
                        publishedDateTime = LocalDateTime.parse(cleanDate, API_DATE_FORMAT);
                    }
                    article.setPublishedAt(publishedDateTime);
                } catch (Exception e) {
                    logger.debug("Error parsing published date: {}, using current time", publishedAt);
                    article.setPublishedAt(LocalDateTime.now());
                }
            } else {
                article.setPublishedAt(LocalDateTime.now());
            }
            
            article.setCachedAt(LocalDateTime.now());
            
            return article;
            
        } catch (Exception e) {
            logger.error("Error parsing article node", e);
            return null;
        }
    }
    
    /**
     * Get string value from JSON node
     */
    private String getStringValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asText() : null;
    }
    
    /**
     * Get string value from nested JSON node
     */
    private String getStringValue(JsonNode node, String parentField, String childField) {
        JsonNode parentNode = node.get(parentField);
        if (parentNode != null) {
            return getStringValue(parentNode, childField);
        }
        return null;
    }
    
    /**
     * Validate article data
     */
    private boolean isValidArticle(NewsArticle article) {
        return article.getTitle() != null && !article.getTitle().trim().isEmpty() &&
               article.getUrl() != null && !article.getUrl().trim().isEmpty() &&
               article.getTitle().length() > 10 && // Minimum title length
               article.getUrl().startsWith("http"); // Valid URL
    }
    
    /**
     * Test API connection
     */
    public boolean testConnection() {
        try {
            List<NewsArticle> testArticles = fetchNews(null, null, 1);
            return !testArticles.isEmpty();
        } catch (Exception e) {
            logger.error("API connection test failed", e);
            return false;
        }
    }
    
    /**
     * Close HTTP client
     */
    public void close() {
        try {
            if (httpClient != null) {
                httpClient.close();
            }
        } catch (IOException e) {
            logger.error("Error closing HTTP client", e);
        }
    }
}
