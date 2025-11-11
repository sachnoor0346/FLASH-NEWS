<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search News - FlashNews</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body>
    <!-- Header -->
    <header class="header">
        <div class="container">
            <div class="header-content">
                <div class="logo">
                    <i class="fas fa-bolt"></i>
                    <h1>FlashNews</h1>
                </div>
                <nav class="nav">
                    <a href="${pageContext.request.contextPath}/news" class="nav-link">Latest</a>
                    <a href="${pageContext.request.contextPath}/news?action=trending" class="nav-link">Trending</a>
                    <a href="${pageContext.request.contextPath}/search.jsp" class="nav-link active">Search</a>
                </nav>
            </div>
        </div>
    </header>

    <!-- Search Section -->
    <section class="search-section">
        <div class="container">
            <div class="search-header">
                <h2><i class="fas fa-search"></i> Search News</h2>
                <p>Find the latest news articles by keyword, category, or location</p>
            </div>
            
            <div class="search-form-container">
                <form method="POST" action="${pageContext.request.contextPath}/news" class="search-form">
                    <input type="hidden" name="action" value="search">
                    
                    <div class="search-input-group">
                        <div class="search-field">
                            <input type="text" 
                                   name="keyword" 
                                   id="keyword" 
                                   placeholder="Enter search keywords..." 
                                   value="${keyword}"
                                   required
                                   class="search-input">
                            <button type="submit" class="search-btn">
                                <i class="fas fa-search"></i> Search
                            </button>
                        </div>
                    </div>
                    
                    <div class="search-filters">
                        <div class="filter-group">
                            <label for="category">Category:</label>
                            <select name="category" id="category" class="filter-select">
                                <option value="">All Categories</option>
                                <c:forEach var="category" items="${categories}">
                                    <option value="${category.id}" ${categoryId == category.id ? 'selected' : ''}>
                                        ${category.displayName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        
                        <div class="filter-group">
                            <label for="location">Location:</label>
                            <select name="location" id="location" class="filter-select">
                                <option value="">All Locations</option>
                                <c:forEach var="location" items="${locations}">
                                    <option value="${location.id}" ${locationId == location.id ? 'selected' : ''}>
                                        ${location.displayName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        
                        <div class="filter-group">
                            <label for="limit">Results:</label>
                            <select name="limit" id="limit" class="filter-select">
                                <option value="10" ${limit == 10 ? 'selected' : ''}>10 articles</option>
                                <option value="20" ${limit == 20 ? 'selected' : ''}>20 articles</option>
                                <option value="50" ${limit == 50 ? 'selected' : ''}>50 articles</option>
                                <option value="100" ${limit == 100 ? 'selected' : ''}>100 articles</option>
                            </select>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </section>

    <!-- Search Results -->
    <main class="main-content">
        <div class="container">
            <c:choose>
                <c:when test="${not empty keyword}">
                    <div class="search-results-header">
                        <h3>
                            <i class="fas fa-search"></i> Search Results for "${keyword}"
                        </h3>
                        <p class="results-count">${articles.size()} articles found</p>
                    </div>
                    
                    <c:choose>
                        <c:when test="${empty articles}">
                            <div class="no-results">
                                <i class="fas fa-search"></i>
                                <h4>No articles found</h4>
                                <p>Try adjusting your search terms or filters.</p>
                                <div class="search-suggestions">
                                    <h5>Search suggestions:</h5>
                                    <ul>
                                        <li>Try different keywords</li>
                                        <li>Check your spelling</li>
                                        <li>Use more general terms</li>
                                        <li>Remove some filters</li>
                                    </ul>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="news-grid">
                                <c:forEach var="article" items="${articles}">
                                    <article class="news-card">
                                        <c:if test="${not empty article.imageUrl}">
                                            <div class="news-image">
                                                <img src="${article.imageUrl}" alt="${article.title}" loading="lazy">
                                                <c:if test="${article.trending}">
                                                    <span class="trending-badge">
                                                        <i class="fas fa-fire"></i> Trending
                                                    </span>
                                                </c:if>
                                            </div>
                                        </c:if>
                                        
                                        <div class="news-content">
                                            <h3 class="news-title">
                                                <a href="${pageContext.request.contextPath}/news?action=article&id=${article.id}">
                                                    ${article.title}
                                                </a>
                                            </h3>
                                            
                                            <p class="news-description">
                                                ${article.shortDescription}
                                            </p>
                                            
                                            <div class="news-meta">
                                                <span class="news-source">
                                                    <i class="fas fa-newspaper"></i> ${article.sourceName}
                                                </span>
                                                <span class="news-date">
                                                    <i class="fas fa-clock"></i> 
                                                    ${article.publishedAt}
                                                </span>
                                                <span class="news-views">
                                                    <i class="fas fa-eye"></i> ${article.viewCount}
                                                </span>
                                            </div>
                                            
                                            <div class="news-actions">
                                                <a href="${article.url}" target="_blank" class="btn btn-outline">
                                                    <i class="fas fa-external-link-alt"></i> Read More
                                                </a>
                                                <a href="${pageContext.request.contextPath}/news?action=article&id=${article.id}" 
                                                   class="btn btn-primary">
                                                    <i class="fas fa-book-open"></i> View Details
                                                </a>
                                            </div>
                                        </div>
                                    </article>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <div class="search-intro">
                        <div class="search-intro-content">
                            <i class="fas fa-search"></i>
                            <h3>Start Your Search</h3>
                            <p>Enter keywords above to search through thousands of news articles</p>
                            
                            <div class="search-tips">
                                <h4>Search Tips:</h4>
                                <ul>
                                    <li>Use specific keywords for better results</li>
                                    <li>Try different combinations of words</li>
                                    <li>Use filters to narrow down results</li>
                                    <li>Search for recent news by adding time-related terms</li>
                                </ul>
                            </div>
                            
                            <div class="popular-searches">
                                <h4>Popular Searches:</h4>
                                <div class="search-tags">
                                    <span class="search-tag" onclick="searchFor('technology')">Technology</span>
                                    <span class="search-tag" onclick="searchFor('sports')">Sports</span>
                                    <span class="search-tag" onclick="searchFor('business')">Business</span>
                                    <span class="search-tag" onclick="searchFor('health')">Health</span>
                                    <span class="search-tag" onclick="searchFor('politics')">Politics</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <div class="footer-content">
                <div class="footer-section">
                    <h4>FlashNews</h4>
                    <p>Your trusted source for the latest news and updates.</p>
                </div>
                <div class="footer-section">
                    <h4>Quick Links</h4>
                    <ul>
                        <li><a href="${pageContext.request.contextPath}/news">Latest News</a></li>
                        <li><a href="${pageContext.request.contextPath}/news?action=trending">Trending</a></li>
                        <li><a href="${pageContext.request.contextPath}/search.jsp">Search</a></li>
                    </ul>
                </div>
                <div class="footer-section">
                    <h4>Categories</h4>
                    <ul>
                        <c:forEach var="category" items="${categories}" begin="0" end="4">
                            <li><a href="${pageContext.request.contextPath}/news?category=${category.id}">${category.displayName}</a></li>
                        </c:forEach>
                    </ul>
                </div>
            </div>
            <div class="footer-bottom">
                <p>&copy; 2024 FlashNews. All rights reserved.</p>
            </div>
        </div>
    </footer>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        function searchFor(keyword) {
            document.getElementById('keyword').value = keyword;
            document.querySelector('.search-form').submit();
        }
    </script>
    
    <style>
        .search-section {
            background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
            color: white;
            padding: 3rem 0;
        }
        
        .search-header {
            text-align: center;
            margin-bottom: 2rem;
        }
        
        .search-header h2 {
            font-size: 2.5rem;
            margin-bottom: 1rem;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
        }
        
        .search-form-container {
            max-width: 800px;
            margin: 0 auto;
        }
        
        .search-form {
            background: white;
            padding: 2rem;
            border-radius: var(--border-radius);
            box-shadow: var(--shadow-heavy);
        }
        
        .search-field {
            display: flex;
            margin-bottom: 1.5rem;
        }
        
        .search-input {
            flex: 1;
            padding: 1rem;
            border: 2px solid var(--border-color);
            border-radius: var(--border-radius) 0 0 var(--border-radius);
            font-size: 1.1rem;
            outline: none;
        }
        
        .search-input:focus {
            border-color: var(--primary-color);
        }
        
        .search-btn {
            background: var(--primary-color);
            color: white;
            border: none;
            padding: 1rem 2rem;
            border-radius: 0 var(--border-radius) var(--border-radius) 0;
            font-size: 1.1rem;
            font-weight: 600;
            cursor: pointer;
            transition: var(--transition);
        }
        
        .search-btn:hover {
            background: var(--primary-dark);
        }
        
        .search-filters {
            display: flex;
            gap: 1rem;
            flex-wrap: wrap;
        }
        
        .search-results-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
            padding-bottom: 1rem;
            border-bottom: 2px solid var(--primary-color);
        }
        
        .search-results-header h3 {
            color: var(--primary-color);
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .no-results {
            text-align: center;
            padding: 4rem 2rem;
            color: var(--text-secondary);
        }
        
        .no-results i {
            font-size: 4rem;
            color: var(--text-light);
            margin-bottom: 1rem;
        }
        
        .search-suggestions {
            margin-top: 2rem;
            text-align: left;
            max-width: 400px;
            margin-left: auto;
            margin-right: auto;
        }
        
        .search-suggestions ul {
            list-style: none;
            padding: 0;
        }
        
        .search-suggestions li {
            padding: 0.5rem 0;
            border-bottom: 1px solid var(--border-color);
        }
        
        .search-intro {
            text-align: center;
            padding: 4rem 2rem;
        }
        
        .search-intro-content i {
            font-size: 4rem;
            color: var(--primary-color);
            margin-bottom: 1rem;
        }
        
        .search-tips,
        .popular-searches {
            margin-top: 2rem;
            text-align: left;
            max-width: 600px;
            margin-left: auto;
            margin-right: auto;
        }
        
        .search-tags {
            display: flex;
            flex-wrap: wrap;
            gap: 0.5rem;
            margin-top: 1rem;
        }
        
        .search-tag {
            background: var(--primary-light);
            color: var(--primary-color);
            padding: 0.5rem 1rem;
            border-radius: 20px;
            cursor: pointer;
            transition: var(--transition);
            font-weight: 500;
        }
        
        .search-tag:hover {
            background: var(--primary-color);
            color: white;
        }
        
        @media (max-width: 768px) {
            .search-field {
                flex-direction: column;
            }
            
            .search-input {
                border-radius: var(--border-radius);
                margin-bottom: 1rem;
            }
            
            .search-btn {
                border-radius: var(--border-radius);
            }
            
            .search-filters {
                flex-direction: column;
            }
            
            .search-results-header {
                flex-direction: column;
                align-items: flex-start;
                gap: 1rem;
            }
        }
    </style>
</body>
</html>
