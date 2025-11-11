<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FlashNews - Smart News Flash</title>
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
                    <a href="${pageContext.request.contextPath}/news" class="nav-link active">Latest</a>
                    <a href="${pageContext.request.contextPath}/news?action=trending" class="nav-link">Trending</a>
                    <a href="${pageContext.request.contextPath}/search.jsp" class="nav-link">Search</a>
                </nav>
            </div>
        </div>
    </header>

    <!-- Filter Section -->
    <section class="filter-section">
        <div class="container">
            <div class="filter-controls">
                <form method="GET" action="${pageContext.request.contextPath}/news" class="filter-form">
                    <input type="hidden" name="action" value="list">
                    
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
                        <label for="filter">Filter:</label>
                        <select name="filter" id="filter" class="filter-select">
                            <option value="latest" ${filterType == 'latest' ? 'selected' : ''}>Latest</option>
                            <option value="trending" ${filterType == 'trending' ? 'selected' : ''}>Trending</option>
                            <option value="local" ${filterType == 'local' ? 'selected' : ''}>Local Only</option>
                        </select>
                    </div>
                    
                    <button type="submit" class="filter-btn">
                        <i class="fas fa-filter"></i> Apply Filters
                    </button>
                </form>
                
                <div class="refresh-section">
                    <form method="POST" action="${pageContext.request.contextPath}/news" class="refresh-form">
                        <input type="hidden" name="action" value="refresh">
                        <input type="hidden" name="category" value="${categoryId}">
                        <input type="hidden" name="location" value="${locationId}">
                        <button type="submit" class="refresh-btn">
                            <i class="fas fa-sync-alt"></i> Refresh News
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </section>

    <!-- Main Content -->
    <main class="main-content">
        <div class="container">
            <div class="news-header">
                <h2>
                    <c:choose>
                        <c:when test="${filterType == 'trending'}">
                            <i class="fas fa-fire"></i> Trending News
                        </c:when>
                        <c:when test="${filterType == 'local'}">
                            <i class="fas fa-map-marker-alt"></i> Local News
                        </c:when>
                        <c:otherwise>
                            <i class="fas fa-clock"></i> Latest News
                        </c:otherwise>
                    </c:choose>
                </h2>
                <p class="news-count">${articles.size()} articles found</p>
            </div>
            
            <div class="news-grid">
                <c:choose>
                    <c:when test="${empty articles}">
                        <div class="no-news">
                            <i class="fas fa-newspaper"></i>
                            <h3>No news articles found</h3>
                            <p>Try adjusting your filters or refresh the news cache.</p>
                            <a href="${pageContext.request.contextPath}/news" class="btn btn-primary">View All News</a>
                        </div>
                    </c:when>
                    <c:otherwise>
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
                    </c:otherwise>
                </c:choose>
            </div>
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
</body>
</html>
