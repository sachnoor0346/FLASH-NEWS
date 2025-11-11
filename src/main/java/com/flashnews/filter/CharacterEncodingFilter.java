package com.flashnews.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CharacterEncodingFilter to ensure UTF-8 encoding for all requests and responses
 * Demonstrates filter pattern and request/response preprocessing
 */
@WebFilter("/*")
public class CharacterEncodingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(CharacterEncodingFilter.class);
    private String encoding = "UTF-8";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String encodingParam = filterConfig.getInitParameter("encoding");
        if (encodingParam != null && !encodingParam.trim().isEmpty()) {
            this.encoding = encodingParam;
        }
        logger.info("CharacterEncodingFilter initialized with encoding: {}", encoding);
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Set character encoding for request
        if (httpRequest.getCharacterEncoding() == null) {
            httpRequest.setCharacterEncoding(encoding);
        }
        
        // Set character encoding for response
        httpResponse.setCharacterEncoding(encoding);
        httpResponse.setContentType("text/html; charset=" + encoding);
        
        // Set additional headers for better browser compatibility
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setHeader("Expires", "0");
        
        logger.debug("Character encoding set to {} for request: {}", encoding, httpRequest.getRequestURI());
        
        // Continue with the filter chain
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        logger.info("CharacterEncodingFilter destroyed");
    }
}
