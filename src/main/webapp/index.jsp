<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
                    <a href="${pageContext.request.contextPath}/news" class="nav-link">Latest</a>
                    <a href="${pageContext.request.contextPath}/news?action=trending" class="nav-link">Trending</a>
                    <a href="${pageContext.request.contextPath}/search.jsp" class="nav-link">Search</a>
                </nav>
            </div>
        </div>
    </header>

    <!-- Hero Section -->
    <section class="hero">
        <div class="container">
            <div class="hero-content">
                <h2>Stay Informed with FlashNews</h2>
                <p>Get the latest news updates instantly with our smart news flash system. 
                   Filter by category, location, and trending topics.</p>
                <div class="hero-actions">
                    <a href="${pageContext.request.contextPath}/news" class="btn btn-primary btn-large">
                        <i class="fas fa-newspaper"></i> View Latest News
                    </a>
                    <a href="${pageContext.request.contextPath}/news?action=trending" class="btn btn-outline btn-large">
                        <i class="fas fa-fire"></i> Trending Now
                    </a>
                </div>
            </div>
        </div>
    </section>

    <!-- Features Section -->
    <section class="features">
        <div class="container">
            <h3 class="section-title">Why Choose FlashNews?</h3>
            <div class="features-grid">
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-bolt"></i>
                    </div>
                    <h4>Lightning Fast</h4>
                    <p>Get news updates instantly with our optimized caching system and real-time API integration.</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-filter"></i>
                    </div>
                    <h4>Smart Filtering</h4>
                    <p>Filter news by category, location, and trending status to find exactly what you're looking for.</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-mobile-alt"></i>
                    </div>
                    <h4>Responsive Design</h4>
                    <p>Access FlashNews on any device with our fully responsive and mobile-optimized interface.</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-search"></i>
                    </div>
                    <h4>Advanced Search</h4>
                    <p>Search through thousands of articles with our powerful search functionality and keyword matching.</p>
                </div>
            </div>
        </div>
    </section>

    <!-- Quick News Preview -->
    <section class="news-preview">
        <div class="container">
            <h3 class="section-title">Latest News Headlines</h3>
            <div class="preview-grid">
                <div class="preview-card">
                    <div class="preview-content">
                        <h4>Technology News</h4>
                        <p>Stay updated with the latest in technology, AI, and digital innovation.</p>
                        <a href="${pageContext.request.contextPath}/news?category=1" class="btn btn-outline">
                            View Technology News
                        </a>
                    </div>
                </div>
                <div class="preview-card">
                    <div class="preview-content">
                        <h4>Sports Updates</h4>
                        <p>Get the latest scores, highlights, and sports news from around the world.</p>
                        <a href="${pageContext.request.contextPath}/news?category=2" class="btn btn-outline">
                            View Sports News
                        </a>
                    </div>
                </div>
                <div class="preview-card">
                    <div class="preview-content">
                        <h4>Business & Finance</h4>
                        <p>Stay informed about market trends, business news, and economic updates.</p>
                        <a href="${pageContext.request.contextPath}/news?category=3" class="btn btn-outline">
                            View Business News
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <div class="footer-content">
                <div class="footer-section">
                    <h4>FlashNews</h4>
                    <p>Your trusted source for the latest news and updates. Built with Java, JDBC, and modern web technologies.</p>
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
                        <li><a href="${pageContext.request.contextPath}/news?category=1">Technology</a></li>
                        <li><a href="${pageContext.request.contextPath}/news?category=2">Sports</a></li>
                        <li><a href="${pageContext.request.contextPath}/news?category=3">Business</a></li>
                        <li><a href="${pageContext.request.contextPath}/news?category=4">Health</a></li>
                        <li><a href="${pageContext.request.contextPath}/news?category=5">Entertainment</a></li>
                    </ul>
                </div>
            </div>
            <div class="footer-bottom">
                <p>&copy; 2024 FlashNews. All rights reserved. | Built with Java, JDBC, and News API</p>
            </div>
        </div>
    </footer>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>

<style>
/* Additional styles for homepage */
.hero {
    background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
    color: white;
    padding: 4rem 0;
    text-align: center;
}

.hero-content h2 {
    font-size: 3rem;
    margin-bottom: 1rem;
    font-weight: 700;
}

.hero-content p {
    font-size: 1.2rem;
    margin-bottom: 2rem;
    max-width: 600px;
    margin-left: auto;
    margin-right: auto;
    opacity: 0.9;
}

.hero-actions {
    display: flex;
    gap: 1rem;
    justify-content: center;
    flex-wrap: wrap;
}

.btn-large {
    padding: 1rem 2rem;
    font-size: 1.1rem;
}

.features {
    padding: 4rem 0;
    background-color: var(--surface-color);
}

.section-title {
    text-align: center;
    font-size: 2.5rem;
    color: var(--primary-color);
    margin-bottom: 3rem;
    font-weight: 700;
}

.features-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 2rem;
}

.feature-card {
    text-align: center;
    padding: 2rem;
    background-color: white;
    border-radius: var(--border-radius);
    box-shadow: var(--shadow-light);
    transition: var(--transition);
}

.feature-card:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-medium);
}

.feature-icon {
    width: 80px;
    height: 80px;
    background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 1.5rem;
    color: white;
    font-size: 2rem;
}

.feature-card h4 {
    font-size: 1.3rem;
    margin-bottom: 1rem;
    color: var(--text-primary);
}

.feature-card p {
    color: var(--text-secondary);
    line-height: 1.6;
}

.news-preview {
    padding: 4rem 0;
}

.preview-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 2rem;
}

.preview-card {
    background: linear-gradient(135deg, var(--primary-light), white);
    border-radius: var(--border-radius);
    padding: 2rem;
    border: 1px solid var(--border-color);
    transition: var(--transition);
}

.preview-card:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-medium);
}

.preview-content h4 {
    color: var(--primary-color);
    font-size: 1.3rem;
    margin-bottom: 1rem;
}

.preview-content p {
    color: var(--text-secondary);
    margin-bottom: 1.5rem;
    line-height: 1.6;
}

@media (max-width: 768px) {
    .hero-content h2 {
        font-size: 2rem;
    }
    
    .hero-content p {
        font-size: 1rem;
    }
    
    .hero-actions {
        flex-direction: column;
        align-items: center;
    }
    
    .section-title {
        font-size: 2rem;
    }
}
</style>
