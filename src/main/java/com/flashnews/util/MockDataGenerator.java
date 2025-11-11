package com.flashnews.util;

import com.flashnews.model.NewsArticle;
import com.flashnews.model.Category;
import com.flashnews.model.Location;
import com.flashnews.dao.NewsArticleDAO;
import com.flashnews.dao.CategoryDAO;
import com.flashnews.dao.LocationDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * MockDataGenerator class for creating sample data for demonstration
 * This ensures the application has data to show even without API access
 */
public class MockDataGenerator {
    private static final Logger logger = LoggerFactory.getLogger(MockDataGenerator.class);
    
    private final NewsArticleDAO newsArticleDAO;
    private final CategoryDAO categoryDAO;
    private final LocationDAO locationDAO;
    
    public MockDataGenerator() {
        this.newsArticleDAO = new NewsArticleDAO();
        this.categoryDAO = new CategoryDAO();
        this.locationDAO = new LocationDAO();
    }
    
    /**
     * Generate mock news articles for demonstration
     */
    public void generateMockData() {
        logger.info("Generating mock data for demonstration...");
        
        try {
            // Get categories and locations
            List<Category> categories = categoryDAO.getAllCategories();
            List<Location> locations = locationDAO.getAllLocations();
            
            if (categories.isEmpty() || locations.isEmpty()) {
                logger.warn("No categories or locations found, skipping mock data generation");
                return;
            }
            
            // Check if we already have enough articles
            var stats = newsArticleDAO.getTotalArticlesCount();
            if (stats > 20) {
                logger.info("Already have {} articles, skipping mock data generation", stats);
                return;
            }
            
            // Create mock news articles
            List<NewsArticle> mockArticles = createMockArticles(categories, locations);
            
            // Insert articles into database
            int insertedCount = 0;
            for (NewsArticle article : mockArticles) {
                try {
                    // Check if article already exists
                    NewsArticle existing = newsArticleDAO.getArticleByUrl(article.getUrl());
                    if (existing == null) {
                        NewsArticle saved = newsArticleDAO.saveArticle(article);
                        if (saved != null) {
                            insertedCount++;
                        }
                    }
                } catch (Exception e) {
                    logger.debug("Error inserting mock article: {}", article.getTitle());
                }
            }
            
            logger.info("Successfully generated {} new mock articles", insertedCount);
            
        } catch (Exception e) {
            logger.error("Error generating mock data", e);
        }
    }
    
    /**
     * Create mock news articles
     */
    private List<NewsArticle> createMockArticles(List<Category> categories, List<Location> locations) {
        return Arrays.asList(
            // Technology News
            createMockArticle(
                "Revolutionary AI Technology Transforms Healthcare Industry",
                "A groundbreaking artificial intelligence system has been developed that can diagnose diseases with 99% accuracy, potentially saving millions of lives worldwide.",
                "https://example.com/ai-healthcare",
                "https://via.placeholder.com/600x400/FF5722/FFFFFF?text=AI+Healthcare",
                "TechNews",
                "https://technews.com",
                1, 1, LocalDateTime.now().minusHours(2)
            ),
            createMockArticle(
                "New Quantum Computer Breaks World Record",
                "Scientists have achieved a major breakthrough in quantum computing, processing complex calculations 1000 times faster than traditional computers.",
                "https://example.com/quantum-computer",
                "https://via.placeholder.com/600x400/2196F3/FFFFFF?text=Quantum+Computing",
                "Science Daily",
                "https://sciencedaily.com",
                1, 2, LocalDateTime.now().minusHours(4)
            ),
            createMockArticle(
                "5G Network Expansion Reaches Rural Areas",
                "Major telecommunications companies announce plans to bring high-speed 5G internet to remote rural communities across the country.",
                "https://example.com/5g-expansion",
                "https://via.placeholder.com/600x400/4CAF50/FFFFFF?text=5G+Network",
                "TechWire",
                "https://techwire.com",
                1, 1, LocalDateTime.now().minusHours(6)
            ),
            
            // Sports News
            createMockArticle(
                "Championship Finals Set for This Weekend",
                "The highly anticipated championship finals will take place this weekend, featuring the top two teams in an epic showdown.",
                "https://example.com/championship-finals",
                "https://via.placeholder.com/600x400/FF9800/FFFFFF?text=Championship",
                "Sports Central",
                "https://sportscentral.com",
                2, 1, LocalDateTime.now().minusHours(1)
            ),
            createMockArticle(
                "Olympic Athlete Breaks World Record",
                "An Olympic athlete has set a new world record in their event, inspiring athletes worldwide with their incredible performance.",
                "https://example.com/olympic-record",
                "https://via.placeholder.com/600x400/E91E63/FFFFFF?text=Olympic+Record",
                "Olympic News",
                "https://olympicnews.com",
                2, 3, LocalDateTime.now().minusHours(3)
            ),
            
            // Business News
            createMockArticle(
                "Stock Market Reaches All-Time High",
                "The stock market has reached unprecedented heights, with major indices showing strong growth across all sectors.",
                "https://example.com/stock-market-high",
                "https://via.placeholder.com/600x400/8BC34A/FFFFFF?text=Stock+Market",
                "Business Today",
                "https://businesstoday.com",
                3, 1, LocalDateTime.now().minusHours(5)
            ),
            createMockArticle(
                "New Startup Raises $50 Million in Funding",
                "A promising tech startup has successfully raised $50 million in Series A funding, planning to expand globally.",
                "https://example.com/startup-funding",
                "https://via.placeholder.com/600x400/9C27B0/FFFFFF?text=Startup+Funding",
                "Venture News",
                "https://venturenews.com",
                3, 2, LocalDateTime.now().minusHours(7)
            ),
            
            // Health News
            createMockArticle(
                "Breakthrough in Cancer Treatment Research",
                "Medical researchers have discovered a new treatment method that shows promising results in early-stage cancer patients.",
                "https://example.com/cancer-treatment",
                "https://via.placeholder.com/600x400/F44336/FFFFFF?text=Cancer+Research",
                "Medical News",
                "https://medicalnews.com",
                4, 1, LocalDateTime.now().minusHours(8)
            ),
            createMockArticle(
                "Mental Health Awareness Campaign Launched",
                "A nationwide mental health awareness campaign has been launched to provide support and resources for those in need.",
                "https://example.com/mental-health-campaign",
                "https://via.placeholder.com/600x400/607D8B/FFFFFF?text=Mental+Health",
                "Health Weekly",
                "https://healthweekly.com",
                4, 2, LocalDateTime.now().minusHours(10)
            ),
            
            // Entertainment News
            createMockArticle(
                "Blockbuster Movie Breaks Box Office Records",
                "The latest blockbuster movie has shattered box office records, becoming the highest-grossing film of the year.",
                "https://example.com/box-office-record",
                "https://via.placeholder.com/600x400/FF5722/FFFFFF?text=Box+Office",
                "Entertainment Weekly",
                "https://entertainmentweekly.com",
                5, 1, LocalDateTime.now().minusHours(12)
            ),
            createMockArticle(
                "Award-Winning Actor Announces New Project",
                "An acclaimed actor has announced their next major project, generating excitement among fans worldwide.",
                "https://example.com/actor-new-project",
                "https://via.placeholder.com/600x400/795548/FFFFFF?text=Actor+Project",
                "Celebrity News",
                "https://celebritynews.com",
                5, 3, LocalDateTime.now().minusHours(14)
            ),
            
            // Science News
            createMockArticle(
                "Mars Rover Discovers Signs of Ancient Life",
                "NASA's Mars rover has discovered compelling evidence of ancient microbial life, a groundbreaking discovery in space exploration.",
                "https://example.com/mars-life-discovery",
                "https://via.placeholder.com/600x400/FF6F00/FFFFFF?text=Mars+Discovery",
                "Space News",
                "https://spacenews.com",
                6, 1, LocalDateTime.now().minusHours(16)
            ),
            createMockArticle(
                "Climate Change Research Shows Urgent Action Needed",
                "New climate research reveals that immediate action is required to prevent catastrophic environmental damage.",
                "https://example.com/climate-research",
                "https://via.placeholder.com/600x400/4CAF50/FFFFFF?text=Climate+Research",
                "Environmental News",
                "https://environmentalnews.com",
                6, 2, LocalDateTime.now().minusHours(18)
            ),
            
            // Politics News
            createMockArticle(
                "New Legislation Aims to Improve Education System",
                "Lawmakers have introduced new legislation designed to enhance the education system and provide better opportunities for students.",
                "https://example.com/education-legislation",
                "https://via.placeholder.com/600x400/3F51B5/FFFFFF?text=Education+Policy",
                "Political Times",
                "https://politicaltimes.com",
                7, 1, LocalDateTime.now().minusHours(20)
            ),
            createMockArticle(
                "International Summit Addresses Global Challenges",
                "World leaders gather for an international summit to discuss solutions to pressing global challenges.",
                "https://example.com/international-summit",
                "https://via.placeholder.com/600x400/009688/FFFFFF?text=Global+Summit",
                "World News",
                "https://worldnews.com",
                7, 2, LocalDateTime.now().minusHours(22)
            ),
            
            // World News
            createMockArticle(
                "Peace Agreement Signed Between Rival Nations",
                "After years of negotiations, two rival nations have signed a historic peace agreement, bringing hope for regional stability.",
                "https://example.com/peace-agreement",
                "https://via.placeholder.com/600x400/8BC34A/FFFFFF?text=Peace+Agreement",
                "Global Times",
                "https://globaltimes.com",
                8, 1, LocalDateTime.now().minusHours(24)
            ),
            createMockArticle(
                "Humanitarian Aid Reaches Disaster-Stricken Region",
                "International humanitarian aid has successfully reached a disaster-stricken region, providing relief to thousands of affected families.",
                "https://example.com/humanitarian-aid",
                "https://via.placeholder.com/600x400/FF9800/FFFFFF?text=Humanitarian+Aid",
                "Relief News",
                "https://reliefnews.com",
                8, 3, LocalDateTime.now().minusDays(1)
            ),
            
            // Additional Technology News
            createMockArticle(
                "New Smartphone Breaks Sales Records",
                "The latest smartphone release has shattered all previous sales records, with millions of units sold in the first week.",
                "https://example.com/smartphone-sales",
                "https://via.placeholder.com/600x400/3F51B5/FFFFFF?text=Smartphone",
                "Tech Review",
                "https://techreview.com",
                1, 1, LocalDateTime.now().minusHours(1)
            ),
            createMockArticle(
                "Electric Vehicle Market Grows 300%",
                "The electric vehicle market has experienced unprecedented growth, with sales increasing by 300% compared to last year.",
                "https://example.com/ev-market-growth",
                "https://via.placeholder.com/600x400/4CAF50/FFFFFF?text=Electric+Vehicle",
                "Auto News",
                "https://autonews.com",
                1, 2, LocalDateTime.now().minusHours(3)
            ),
            
            // Additional Sports News
            createMockArticle(
                "New Stadium Opens in Major City",
                "A state-of-the-art sports stadium has opened in the heart of the city, featuring cutting-edge technology and seating for 50,000 fans.",
                "https://example.com/new-stadium",
                "https://via.placeholder.com/600x400/FF5722/FFFFFF?text=Stadium",
                "Sports Daily",
                "https://sportsdaily.com",
                2, 1, LocalDateTime.now().minusHours(5)
            ),
            createMockArticle(
                "World Cup Qualifiers Begin This Month",
                "The highly anticipated World Cup qualifiers are set to begin this month, with teams from around the world competing for a spot in the finals.",
                "https://example.com/world-cup-qualifiers",
                "https://via.placeholder.com/600x400/2196F3/FFFFFF?text=World+Cup",
                "Football Weekly",
                "https://footballweekly.com",
                2, 3, LocalDateTime.now().minusHours(7)
            ),
            
            // Additional Business News
            createMockArticle(
                "Tech Company Announces Major Expansion",
                "A leading technology company has announced plans for a major expansion, creating thousands of new jobs across multiple countries.",
                "https://example.com/tech-expansion",
                "https://via.placeholder.com/600x400/9C27B0/FFFFFF?text=Tech+Expansion",
                "Business Wire",
                "https://businesswire.com",
                3, 1, LocalDateTime.now().minusHours(9)
            ),
            createMockArticle(
                "Cryptocurrency Market Shows Strong Recovery",
                "The cryptocurrency market has shown signs of strong recovery, with major digital currencies gaining significant value this week.",
                "https://example.com/crypto-recovery",
                "https://via.placeholder.com/600x400/FF9800/FFFFFF?text=Cryptocurrency",
                "Crypto News",
                "https://cryptonews.com",
                3, 2, LocalDateTime.now().minusHours(11)
            ),
            
            // Additional Health News
            createMockArticle(
                "New Vaccine Shows 95% Effectiveness",
                "A new vaccine has been developed that shows 95% effectiveness against a major disease, offering hope for millions of patients worldwide.",
                "https://example.com/new-vaccine",
                "https://via.placeholder.com/600x400/4CAF50/FFFFFF?text=Vaccine",
                "Medical Journal",
                "https://medicaljournal.com",
                4, 1, LocalDateTime.now().minusHours(13)
            ),
            createMockArticle(
                "Mental Health Awareness Week Launched",
                "A nationwide mental health awareness week has been launched, featuring events and resources to support mental wellbeing in communities.",
                "https://example.com/mental-health-week",
                "https://via.placeholder.com/600x400/607D8B/FFFFFF?text=Mental+Health",
                "Health Today",
                "https://healthtoday.com",
                4, 3, LocalDateTime.now().minusHours(15)
            )
        );
    }
    
    /**
     * Create a mock news article
     */
    private NewsArticle createMockArticle(String title, String description, String url, 
                                        String imageUrl, String sourceName, String sourceUrl,
                                        int categoryId, int locationId, LocalDateTime publishedAt) {
        NewsArticle article = new NewsArticle(title, description, url, sourceName);
        article.setImageUrl(imageUrl);
        article.setSourceUrl(sourceUrl);
        article.setContent(description + " This is additional content for the full article. " +
                          "The story continues with more details about the topic and its implications. " +
                          "Experts weigh in on the significance of this development and what it means for the future.");
        article.setCategoryId(categoryId);
        article.setLocationId(locationId);
        article.setPublishedAt(publishedAt);
        article.setCachedAt(LocalDateTime.now());
        article.setTrending(Math.random() > 0.7); // 30% chance of being trending
        article.setViewCount((int)(Math.random() * 1000)); // Random view count
        
        return article;
    }
}
