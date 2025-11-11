<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - FlashNews</title>
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

    <!-- Error Content -->
    <main class="error-main">
        <div class="container">
            <div class="error-content">
                <div class="error-icon">
                    <i class="fas fa-exclamation-triangle"></i>
                </div>
                
                <h1 class="error-title">
                    <c:choose>
                        <c:when test="${pageContext.errorData.statusCode == 404}">
                            Page Not Found
                        </c:when>
                        <c:when test="${pageContext.errorData.statusCode == 500}">
                            Internal Server Error
                        </c:when>
                        <c:otherwise>
                            Something Went Wrong
                        </c:otherwise>
                    </c:choose>
                </h1>
                
                <p class="error-message">
                    <c:choose>
                        <c:when test="${pageContext.errorData.statusCode == 404}">
                            The page you're looking for doesn't exist or has been moved.
                        </c:when>
                        <c:when test="${pageContext.errorData.statusCode == 500}">
                            We're experiencing technical difficulties. Please try again later.
                        </c:when>
                        <c:otherwise>
                            An unexpected error occurred. Please try again.
                        </c:otherwise>
                    </c:choose>
                </p>
                
                <div class="error-actions">
                    <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
                        <i class="fas fa-home"></i> Go Home
                    </a>
                    <a href="${pageContext.request.contextPath}/news" class="btn btn-outline">
                        <i class="fas fa-newspaper"></i> View News
                    </a>
                    <button onclick="history.back()" class="btn btn-outline">
                        <i class="fas fa-arrow-left"></i> Go Back
                    </button>
                </div>
                
                <c:if test="${pageContext.errorData.statusCode == 500 && not empty pageContext.exception}">
                    <details class="error-details">
                        <summary>Technical Details</summary>
                        <div class="error-stack">
                            <pre><c:out value="${pageContext.exception.stackTrace}" /></pre>
                        </div>
                    </details>
                </c:if>
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
                    <h4>Help</h4>
                    <ul>
                        <li><a href="${pageContext.request.contextPath}/">Home</a></li>
                        <li><a href="${pageContext.request.contextPath}/news">Browse News</a></li>
                        <li><a href="${pageContext.request.contextPath}/search.jsp">Search</a></li>
                    </ul>
                </div>
            </div>
            <div class="footer-bottom">
                <p>&copy; 2024 FlashNews. All rights reserved.</p>
            </div>
        </div>
    </footer>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    
    <style>
        .error-main {
            min-height: 60vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 4rem 0;
        }
        
        .error-content {
            text-align: center;
            max-width: 600px;
            margin: 0 auto;
        }
        
        .error-icon {
            font-size: 6rem;
            color: var(--primary-color);
            margin-bottom: 2rem;
        }
        
        .error-title {
            font-size: 3rem;
            color: var(--text-primary);
            margin-bottom: 1rem;
            font-weight: 700;
        }
        
        .error-message {
            font-size: 1.2rem;
            color: var(--text-secondary);
            margin-bottom: 2rem;
            line-height: 1.6;
        }
        
        .error-actions {
            display: flex;
            gap: 1rem;
            justify-content: center;
            flex-wrap: wrap;
            margin-bottom: 2rem;
        }
        
        .error-details {
            margin-top: 2rem;
            text-align: left;
            background: #f5f5f5;
            border-radius: var(--border-radius);
            padding: 1rem;
        }
        
        .error-details summary {
            cursor: pointer;
            font-weight: 600;
            color: var(--primary-color);
            margin-bottom: 1rem;
        }
        
        .error-stack {
            background: #2d2d2d;
            color: #f8f8f2;
            padding: 1rem;
            border-radius: var(--border-radius);
            overflow-x: auto;
            font-family: 'Courier New', monospace;
            font-size: 0.9rem;
        }
        
        .error-stack pre {
            margin: 0;
            white-space: pre-wrap;
        }
        
        @media (max-width: 768px) {
            .error-title {
                font-size: 2rem;
            }
            
            .error-actions {
                flex-direction: column;
                align-items: center;
            }
            
            .error-actions .btn {
                width: 100%;
                max-width: 300px;
            }
        }
    </style>
</body>
</html>
