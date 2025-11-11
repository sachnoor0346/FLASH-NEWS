package com.flashnews.servlet;

import com.flashnews.model.NewsArticle;
import com.flashnews.model.Category;
import com.flashnews.model.Location;
import com.flashnews.service.NewsService;
import com.flashnews.service.impl.NewsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * NewsServlet class handling news-related HTTP requests
 * Demonstrates MVC pattern with servlets
 */
@WebServlet(name = "NewsServlet", urlPatterns = {"/news", "/api/news"})
public class NewsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(NewsServlet.class);
    private NewsService newsService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.newsService = new NewsServiceImpl();
        logger.info("NewsServlet initialized");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        
        try {
            switch (action) {
                case "list":
                    handleListNews(request, response);
                    break;
                case "trending":
                    handleTrendingNews(request, response);
                    break;
                case "search":
                    handleSearchNews(request, response);
                    break;
                case "article":
                    handleGetArticle(request, response);
                    break;
                case "refresh":
                    handleRefreshCache(request, response);
                    break;
                case "categories":
                    handleGetCategories(request, response);
                    break;
                case "locations":
                    handleGetLocations(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (Exception e) {
            logger.error("Error processing request", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action parameter required");
            return;
        }
        
        try {
            switch (action) {
                case "search":
                    handleSearchNews(request, response);
                    break;
                case "refresh":
                    handleRefreshCache(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (Exception e) {
            logger.error("Error processing POST request", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }
    
    /**
     * Handle listing news articles
     */
    private void handleListNews(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String filterType = request.getParameter("filter");
        if (filterType == null) {
            filterType = "latest";
        }
        
        Integer categoryId = getIntegerParameter(request, "category");
        Integer locationId = getIntegerParameter(request, "location");
        int limit = getIntegerParameter(request, "limit", 20);
        
        List<NewsArticle> articles = newsService.getNewsByFilter(filterType, categoryId, locationId, limit);
        
        // Set request attributes for JSP
        request.setAttribute("articles", articles);
        request.setAttribute("filterType", filterType);
        request.setAttribute("categoryId", categoryId);
        request.setAttribute("locationId", locationId);
        request.setAttribute("limit", limit);
        
        // Forward to JSP
        request.getRequestDispatcher("/WEB-INF/views/news.jsp").forward(request, response);
    }
    
    /**
     * Handle trending news
     */
    private void handleTrendingNews(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Integer categoryId = getIntegerParameter(request, "category");
        Integer locationId = getIntegerParameter(request, "location");
        int limit = getIntegerParameter(request, "limit", 20);
        
        List<NewsArticle> articles = newsService.getTrendingNews(categoryId, locationId, limit);
        
        request.setAttribute("articles", articles);
        request.setAttribute("filterType", "trending");
        request.setAttribute("categoryId", categoryId);
        request.setAttribute("locationId", locationId);
        request.setAttribute("limit", limit);
        
        request.getRequestDispatcher("/WEB-INF/views/news.jsp").forward(request, response);
    }
    
    /**
     * Handle news search
     */
    private void handleSearchNews(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String keyword = request.getParameter("keyword");
        if (keyword == null || keyword.trim().isEmpty()) {
            request.setAttribute("error", "Search keyword is required");
            request.getRequestDispatcher("/WEB-INF/views/search.jsp").forward(request, response);
            return;
        }
        
        Integer categoryId = getIntegerParameter(request, "category");
        Integer locationId = getIntegerParameter(request, "location");
        int limit = getIntegerParameter(request, "limit", 20);
        
        List<NewsArticle> articles = newsService.searchNews(keyword.trim(), categoryId, locationId, limit);
        
        request.setAttribute("articles", articles);
        request.setAttribute("keyword", keyword);
        request.setAttribute("categoryId", categoryId);
        request.setAttribute("locationId", locationId);
        request.setAttribute("limit", limit);
        
        request.getRequestDispatcher("/WEB-INF/views/search.jsp").forward(request, response);
    }
    
    /**
     * Handle getting single article
     */
    private void handleGetArticle(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Integer articleId = getIntegerParameter(request, "id");
        if (articleId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Article ID required");
            return;
        }
        
        NewsArticle article = newsService.getArticleById(articleId);
        if (article == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Article not found");
            return;
        }
        
        request.setAttribute("article", article);
        request.getRequestDispatcher("/WEB-INF/views/article.jsp").forward(request, response);
    }
    
    /**
     * Handle refreshing news cache
     */
    private void handleRefreshCache(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Integer categoryId = getIntegerParameter(request, "category");
        Integer locationId = getIntegerParameter(request, "location");
        
        int refreshedCount = newsService.refreshNewsCache(categoryId, locationId);
        
        request.setAttribute("refreshedCount", refreshedCount);
        request.setAttribute("categoryId", categoryId);
        request.setAttribute("locationId", locationId);
        
        request.getRequestDispatcher("/WEB-INF/views/refresh.jsp").forward(request, response);
    }
    
    /**
     * Handle getting categories
     */
    private void handleGetCategories(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Category> categories = newsService.getAllCategories();
        request.setAttribute("categories", categories);
        
        request.getRequestDispatcher("/WEB-INF/views/categories.jsp").forward(request, response);
    }
    
    /**
     * Handle getting locations
     */
    private void handleGetLocations(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Location> locations = newsService.getAllLocations();
        request.setAttribute("locations", locations);
        
        request.getRequestDispatcher("/WEB-INF/views/locations.jsp").forward(request, response);
    }
    
    /**
     * Get integer parameter from request
     */
    private Integer getIntegerParameter(HttpServletRequest request, String paramName) {
        String paramValue = request.getParameter(paramName);
        if (paramValue != null && !paramValue.trim().isEmpty()) {
            try {
                return Integer.parseInt(paramValue.trim());
            } catch (NumberFormatException e) {
                logger.warn("Invalid integer parameter {}: {}", paramName, paramValue);
            }
        }
        return null;
    }
    
    /**
     * Get integer parameter with default value
     */
    private int getIntegerParameter(HttpServletRequest request, String paramName, int defaultValue) {
        Integer value = getIntegerParameter(request, paramName);
        return value != null ? value : defaultValue;
    }
    
    @Override
    public void destroy() {
        super.destroy();
        if (newsService instanceof NewsServiceImpl) {
            ((NewsServiceImpl) newsService).shutdown();
        }
        logger.info("NewsServlet destroyed");
    }
}
