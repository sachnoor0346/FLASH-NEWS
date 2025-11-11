package com.flashnews.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CacheControlFilter to set appropriate cache headers for static resources
 * Demonstrates filter pattern for resource optimization
 */
@WebFilter(urlPatterns = {"*.css", "*.js", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.svg"})
public class CacheControlFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(CacheControlFilter.class);
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("CacheControlFilter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String extension = getFileExtension(requestURI);
        
        // Set cache headers based on file type
        if (isStaticResource(extension)) {
            setCacheHeaders(httpResponse, extension);
        }
        
        logger.debug("Cache headers set for resource: {}", requestURI);
        
        // Continue with the filter chain
        chain.doFilter(request, response);
    }
    
    /**
     * Set appropriate cache headers based on file extension
     */
    private void setCacheHeaders(HttpServletResponse response, String extension) {
        switch (extension.toLowerCase()) {
            case "css":
            case "js":
                // Cache CSS and JS files for 1 hour
                response.setHeader("Cache-Control", "public, max-age=3600");
                response.setHeader("Expires", getExpirationTime(3600));
                break;
            case "png":
            case "jpg":
            case "jpeg":
            case "gif":
            case "svg":
                // Cache images for 1 day
                response.setHeader("Cache-Control", "public, max-age=86400");
                response.setHeader("Expires", getExpirationTime(86400));
                break;
            default:
                // Default cache for 1 hour
                response.setHeader("Cache-Control", "public, max-age=3600");
                response.setHeader("Expires", getExpirationTime(3600));
        }
        
        // Set ETag for better caching
        response.setHeader("ETag", "\"" + System.currentTimeMillis() + "\"");
    }
    
    /**
     * Check if the file extension represents a static resource
     */
    private boolean isStaticResource(String extension) {
        return extension != null && (
            extension.equals("css") || extension.equals("js") ||
            extension.equals("png") || extension.equals("jpg") ||
            extension.equals("jpeg") || extension.equals("gif") ||
            extension.equals("svg")
        );
    }
    
    /**
     * Get file extension from URI
     */
    private String getFileExtension(String uri) {
        if (uri == null || uri.isEmpty()) {
            return null;
        }
        
        int lastDotIndex = uri.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < uri.length() - 1) {
            return uri.substring(lastDotIndex + 1);
        }
        
        return null;
    }
    
    /**
     * Get expiration time string
     */
    private String getExpirationTime(int maxAgeSeconds) {
        long expirationTime = System.currentTimeMillis() + (maxAgeSeconds * 1000L);
        return new java.util.Date(expirationTime).toGMTString();
    }
    
    @Override
    public void destroy() {
        logger.info("CacheControlFilter destroyed");
    }
}
