/**
 * FlashNews - Main JavaScript file
 * Handles client-side interactions and dynamic functionality
 */

// Global variables
let refreshInterval;
let isLoading = false;

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

/**
 * Initialize the application
 */
function initializeApp() {
    console.log('FlashNews initialized');
    
    // Initialize filter forms
    initializeFilters();
    
    // Initialize auto-refresh
    initializeAutoRefresh();
    
    // Initialize search functionality
    initializeSearch();
    
    // Initialize responsive navigation
    initializeNavigation();
    
    // Initialize loading states
    initializeLoadingStates();
}

/**
 * Initialize filter forms
 */
function initializeFilters() {
    const filterForm = document.querySelector('.filter-form');
    if (filterForm) {
        filterForm.addEventListener('submit', function(e) {
            showLoadingState();
        });
        
        // Auto-submit on filter change
        const filterSelects = filterForm.querySelectorAll('.filter-select');
        filterSelects.forEach(select => {
            select.addEventListener('change', function() {
                if (this.value) {
                    showLoadingState();
                    filterForm.submit();
                }
            });
        });
    }
}

/**
 * Initialize auto-refresh functionality
 */
function initializeAutoRefresh() {
    const refreshBtn = document.querySelector('.refresh-btn');
    if (refreshBtn) {
        refreshBtn.addEventListener('click', function(e) {
            e.preventDefault();
            refreshNews();
        });
    }
    
    // Auto-refresh every 5 minutes
    refreshInterval = setInterval(function() {
        if (!isLoading) {
            refreshNews();
        }
    }, 300000); // 5 minutes
}

/**
 * Refresh news content
 */
function refreshNews() {
    if (isLoading) return;
    
    isLoading = true;
    showLoadingState();
    
    const refreshForm = document.querySelector('.refresh-form');
    if (refreshForm) {
        const formData = new FormData(refreshForm);
        
        fetch(refreshForm.action, {
            method: 'POST',
            body: formData
        })
        .then(response => response.text())
        .then(data => {
            // Reload the page to show updated content
            window.location.reload();
        })
        .catch(error => {
            console.error('Error refreshing news:', error);
            hideLoadingState();
            showNotification('Error refreshing news. Please try again.', 'error');
        })
        .finally(() => {
            isLoading = false;
        });
    }
}

/**
 * Initialize search functionality
 */
function initializeSearch() {
    const searchForm = document.querySelector('form[action*="search"]');
    if (searchForm) {
        searchForm.addEventListener('submit', function(e) {
            const keyword = this.querySelector('input[name="keyword"]');
            if (!keyword || !keyword.value.trim()) {
                e.preventDefault();
                showNotification('Please enter a search keyword', 'warning');
                return;
            }
            showLoadingState();
        });
    }
    
    // Search suggestions (basic implementation)
    const searchInput = document.querySelector('input[name="keyword"]');
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            const value = this.value.trim();
            if (value.length > 2) {
                // Could implement search suggestions here
                console.log('Searching for:', value);
            }
        });
    }
}

/**
 * Initialize responsive navigation
 */
function initializeNavigation() {
    // Mobile menu toggle (if needed)
    const mobileMenuBtn = document.querySelector('.mobile-menu-btn');
    const nav = document.querySelector('.nav');
    
    if (mobileMenuBtn && nav) {
        mobileMenuBtn.addEventListener('click', function() {
            nav.classList.toggle('active');
        });
    }
    
    // Close mobile menu when clicking outside
    document.addEventListener('click', function(e) {
        if (nav && !nav.contains(e.target) && !mobileMenuBtn.contains(e.target)) {
            nav.classList.remove('active');
        }
    });
}

/**
 * Initialize loading states
 */
function initializeLoadingStates() {
    // Add loading states to buttons
    const buttons = document.querySelectorAll('.btn');
    buttons.forEach(btn => {
        btn.addEventListener('click', function() {
            if (this.type === 'submit' || this.classList.contains('refresh-btn')) {
                showButtonLoading(this);
            }
        });
    });
}

/**
 * Show loading state
 */
function showLoadingState() {
    const loadingOverlay = document.createElement('div');
    loadingOverlay.className = 'loading-overlay';
    loadingOverlay.innerHTML = `
        <div class="loading-spinner">
            <div class="loading"></div>
            <p>Loading news...</p>
        </div>
    `;
    
    document.body.appendChild(loadingOverlay);
    
    // Add CSS for loading overlay
    if (!document.querySelector('#loading-styles')) {
        const style = document.createElement('style');
        style.id = 'loading-styles';
        style.textContent = `
            .loading-overlay {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0, 0, 0, 0.5);
                display: flex;
                justify-content: center;
                align-items: center;
                z-index: 9999;
            }
            .loading-spinner {
                background: white;
                padding: 2rem;
                border-radius: 8px;
                text-align: center;
                box-shadow: 0 4px 8px rgba(0,0,0,0.2);
            }
            .loading-spinner p {
                margin-top: 1rem;
                color: #666;
            }
        `;
        document.head.appendChild(style);
    }
}

/**
 * Hide loading state
 */
function hideLoadingState() {
    const loadingOverlay = document.querySelector('.loading-overlay');
    if (loadingOverlay) {
        loadingOverlay.remove();
    }
}

/**
 * Show button loading state
 */
function showButtonLoading(button) {
    const originalText = button.innerHTML;
    button.innerHTML = '<div class="loading"></div> Loading...';
    button.disabled = true;
    
    // Reset after 3 seconds (fallback)
    setTimeout(() => {
        button.innerHTML = originalText;
        button.disabled = false;
    }, 3000);
}

/**
 * Show notification
 */
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <span class="notification-message">${message}</span>
            <button class="notification-close">&times;</button>
        </div>
    `;
    
    // Add CSS for notifications
    if (!document.querySelector('#notification-styles')) {
        const style = document.createElement('style');
        style.id = 'notification-styles';
        style.textContent = `
            .notification {
                position: fixed;
                top: 20px;
                right: 20px;
                background: white;
                border-left: 4px solid #D32F2F;
                box-shadow: 0 4px 8px rgba(0,0,0,0.2);
                border-radius: 4px;
                padding: 1rem;
                z-index: 10000;
                max-width: 400px;
                animation: slideIn 0.3s ease;
            }
            .notification-error {
                border-left-color: #f44336;
            }
            .notification-warning {
                border-left-color: #ff9800;
            }
            .notification-success {
                border-left-color: #4caf50;
            }
            .notification-content {
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            .notification-close {
                background: none;
                border: none;
                font-size: 1.5rem;
                cursor: pointer;
                color: #666;
                margin-left: 1rem;
            }
            @keyframes slideIn {
                from { transform: translateX(100%); opacity: 0; }
                to { transform: translateX(0); opacity: 1; }
            }
        `;
        document.head.appendChild(style);
    }
    
    document.body.appendChild(notification);
    
    // Auto-remove after 5 seconds
    setTimeout(() => {
        notification.remove();
    }, 5000);
    
    // Close button functionality
    notification.querySelector('.notification-close').addEventListener('click', () => {
        notification.remove();
    });
}

/**
 * Format date for display
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diffInHours = (now - date) / (1000 * 60 * 60);
    
    if (diffInHours < 1) {
        return 'Just now';
    } else if (diffInHours < 24) {
        return `${Math.floor(diffInHours)} hours ago`;
    } else {
        return date.toLocaleDateString();
    }
}

/**
 * Handle image loading errors
 */
function handleImageError(img) {
    img.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjVmNWY1Ii8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OTk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPk5vIEltYWdlPC90ZXh0Pjwvc3ZnPg==';
    img.alt = 'No image available';
}

/**
 * Initialize image error handling
 */
function initializeImageErrorHandling() {
    const images = document.querySelectorAll('img');
    images.forEach(img => {
        img.addEventListener('error', function() {
            handleImageError(this);
        });
    });
}

/**
 * Smooth scroll to top
 */
function scrollToTop() {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
}

/**
 * Add scroll to top button
 */
function addScrollToTopButton() {
    const scrollBtn = document.createElement('button');
    scrollBtn.className = 'scroll-to-top';
    scrollBtn.innerHTML = '<i class="fas fa-arrow-up"></i>';
    scrollBtn.setAttribute('aria-label', 'Scroll to top');
    
    // Add CSS for scroll button
    if (!document.querySelector('#scroll-button-styles')) {
        const style = document.createElement('style');
        style.id = 'scroll-button-styles';
        style.textContent = `
            .scroll-to-top {
                position: fixed;
                bottom: 20px;
                right: 20px;
                width: 50px;
                height: 50px;
                background: #D32F2F;
                color: white;
                border: none;
                border-radius: 50%;
                cursor: pointer;
                font-size: 1.2rem;
                box-shadow: 0 4px 8px rgba(0,0,0,0.2);
                transition: all 0.3s ease;
                z-index: 1000;
                display: none;
            }
            .scroll-to-top:hover {
                background: #B71C1C;
                transform: translateY(-2px);
            }
            .scroll-to-top.show {
                display: flex;
                align-items: center;
                justify-content: center;
            }
        `;
        document.head.appendChild(style);
    }
    
    document.body.appendChild(scrollBtn);
    
    // Show/hide based on scroll position
    window.addEventListener('scroll', function() {
        if (window.pageYOffset > 300) {
            scrollBtn.classList.add('show');
        } else {
            scrollBtn.classList.remove('show');
        }
    });
    
    // Scroll to top on click
    scrollBtn.addEventListener('click', scrollToTop);
}

// Initialize additional features when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    initializeImageErrorHandling();
    addScrollToTopButton();
});

// Cleanup on page unload
window.addEventListener('beforeunload', function() {
    if (refreshInterval) {
        clearInterval(refreshInterval);
    }
});
