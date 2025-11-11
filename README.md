# FlashNews - Smart News Flash Web App

A highly professional news website built with Java, JDBC, and News API integration featuring a modern red-themed UI and responsive design.

## 🚀 Features

### Core Functionality
- **Smart News Filtering**: Filter by category, location, and trending status
- **Real-time News Updates**: Auto-refresh with external News API integration
- **Advanced Search**: Keyword-based search with filters
- **Responsive Design**: Modern UI that works on all devices
- **News Caching**: Optimized performance with database caching

### Technical Features
- **MVC Architecture**: Clean separation of concerns with Servlets and JSP
- **Database Integration**: MySQL with JDBC for data persistence
- **External API Integration**: News API for real-time news updates
- **Connection Pooling**: Efficient database connection management
- **Exception Handling**: Comprehensive error handling and logging
- **Security**: Input validation and SQL injection prevention

## 🛠️ Technology Stack

### Backend
- **Java 11+**: Core programming language
- **Servlets & JSP**: Web application framework
- **JDBC**: Database connectivity
- **MySQL**: Database management system
- **Maven**: Build and dependency management

### Frontend
- **HTML5 & CSS3**: Modern web standards
- **JavaScript**: Client-side interactivity
- **Font Awesome**: Icon library
- **Responsive Design**: Mobile-first approach

### External Services
- **News API**: Real-time news data source
- **Jackson**: JSON processing
- **Apache HTTP Client**: HTTP requests

## 📋 Prerequisites

Before running the application, ensure you have:

- **Java 11 or higher**
- **Maven 3.6 or higher**
- **MySQL 8.0 or higher**
- **Tomcat 9.0 or higher** (or use Maven Tomcat plugin)
- **News API Key** (from https://newsapi.org/)

## 🚀 Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd flashnews
```

### 2. Database Setup
1. Create a MySQL database:
```sql
CREATE DATABASE flashnews_db;
```

2. Update database credentials in `src/main/resources/database.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/flashnews_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=your_username
db.password=your_password
```

3. The database schema will be automatically created on first run.

### 3. News API Configuration
1. Get a free API key from [News API](https://newsapi.org/)
2. Update the API key in `src/main/resources/database.properties`:
```properties
news.api.key=your_news_api_key_here
```

### 4. Build and Run
```bash
# Build the project
mvn clean compile

# Run with Maven Tomcat plugin
mvn tomcat7:run

# Or build WAR file for deployment
mvn clean package
```

### 5. Access the Application
Open your browser and navigate to:
- **Local**: http://localhost:8080/flashnews
- **Homepage**: http://localhost:8080/flashnews/

## 📁 Project Structure

```
flashnews/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/flashnews/
│   │   │       ├── dao/                 # Data Access Objects
│   │   │       ├── database/            # Database connection management
│   │   │       ├── external/            # External API integration
│   │   │       ├── filter/              # Servlet filters
│   │   │       ├── listener/            # Application listeners
│   │   │       ├── model/               # Data models
│   │   │       ├── service/             # Business logic layer
│   │   │       └── servlet/             # Web controllers
│   │   ├── resources/
│   │   │   ├── database/                # Database schema
│   │   │   └── database.properties      # Configuration
│   │   └── webapp/
│   │       ├── css/                     # Stylesheets
│   │       ├── js/                      # JavaScript files
│   │       ├── WEB-INF/
│   │       │   ├── views/               # JSP pages
│   │       │   └── web.xml              # Web configuration
│   │       ├── index.jsp                # Homepage
│   │       └── search.jsp               # Search page
├── pom.xml                              # Maven configuration
└── README.md                            # This file
```

## 🎯 Key Features Explained

### 1. Object-Oriented Programming Concepts

#### Encapsulation
- Private fields with public getters/setters
- Data validation in model classes
- Controlled access to object properties

#### Inheritance
- Service interfaces with implementations
- Base classes for common functionality
- Polymorphism in service layer

#### Abstraction
- Service interfaces defining contracts
- Abstract database operations
- Clean separation of concerns

#### Event-Driven Programming
- Servlet lifecycle management
- Database initialization listeners
- Filter chain processing

### 2. Database Design
- **Normalized Schema**: Efficient data storage
- **Indexes**: Optimized query performance
- **Foreign Keys**: Data integrity
- **Soft Deletes**: Data preservation

### 3. MVC Architecture
- **Model**: Data classes and DAOs
- **View**: JSP pages with JSTL
- **Controller**: Servlets handling requests

### 4. Security Features
- **SQL Injection Prevention**: Prepared statements
- **Input Validation**: Server-side validation
- **XSS Protection**: Output encoding
- **CSRF Protection**: Form tokens

## 🔧 Configuration

### Database Configuration
Edit `src/main/resources/database.properties`:
```properties
# Database settings
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/flashnews_db
db.username=root
db.password=password

# News API settings
news.api.key=your_api_key_here
news.api.url=https://newsapi.org/v2
news.cache.duration=300
```

### Application Settings
Configure in `web.xml`:
- Session timeout
- Error pages
- Security constraints
- Filter mappings

## 📊 Database Schema

### Tables
- **users**: User accounts and preferences
- **categories**: News categories
- **locations**: Geographical locations
- **news_articles**: Cached news articles
- **reading_history**: User reading history
- **search_history**: Search analytics

### Key Relationships
- Articles belong to categories and locations
- Users have preferences and reading history
- Search history tracks user queries

## 🎨 UI/UX Features

### Design Principles
- **Modern Flat Design**: Clean, professional appearance
- **Red Color Palette**: News-appropriate branding
- **Responsive Layout**: Mobile-first design
- **Accessibility**: Keyboard navigation and screen reader support

### User Experience
- **Intuitive Navigation**: Clear menu structure
- **Fast Loading**: Optimized performance
- **Search Functionality**: Advanced filtering
- **Real-time Updates**: Auto-refresh capabilities

## 🚀 Deployment

### Production Deployment
1. Build WAR file: `mvn clean package`
2. Deploy to Tomcat server
3. Configure production database
4. Set up News API key
5. Configure logging levels

### Docker Deployment (Optional)
```dockerfile
FROM tomcat:9.0
COPY target/flashnews.war /usr/local/tomcat/webapps/
EXPOSE 8080
```

## 🧪 Testing

### Manual Testing
1. **Homepage**: Verify layout and navigation
2. **News Listing**: Test filtering and pagination
3. **Search**: Test keyword search functionality
4. **Responsive**: Test on different screen sizes
5. **API Integration**: Verify news updates

### Database Testing
```sql
-- Check data integrity
SELECT COUNT(*) FROM news_articles;
SELECT COUNT(*) FROM categories;
SELECT COUNT(*) FROM locations;

-- Test queries
SELECT * FROM news_articles WHERE is_trending = true;
SELECT * FROM news_articles WHERE category_id = 1;
```

## 🔍 Troubleshooting

### Common Issues

#### Database Connection
- Verify MySQL is running
- Check database credentials
- Ensure database exists

#### News API
- Verify API key is valid
- Check API quota limits
- Monitor network connectivity

#### Build Issues
- Ensure Java 11+ is installed
- Check Maven configuration
- Verify all dependencies

### Logs
Check application logs for detailed error information:
- Database connection issues
- API integration problems
- Servlet errors

## 📈 Performance Optimization

### Database
- Connection pooling
- Query optimization
- Index usage
- Caching strategies

### Application
- Servlet optimization
- JSP compilation
- Static resource caching
- GZIP compression

## 🔒 Security Considerations

### Data Protection
- Input sanitization
- SQL injection prevention
- XSS protection
- CSRF tokens

### Access Control
- User authentication
- Role-based access
- Session management
- Secure headers

## 📝 API Documentation

### Endpoints
- `GET /news` - List news articles
- `GET /news?action=trending` - Trending articles
- `POST /news?action=search` - Search articles
- `GET /news?action=article&id={id}` - Get specific article

### Parameters
- `category` - Filter by category ID
- `location` - Filter by location ID
- `filter` - Filter type (latest, trending, local)
- `limit` - Number of results
- `keyword` - Search keyword

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- **FlashNews Team** - *Initial work* - [GitHub](https://github.com/flashnews)

## 🙏 Acknowledgments

- News API for providing news data
- Font Awesome for icons
- Maven community for build tools
- Java community for excellent documentation

## 📞 Support

For support and questions:
- Create an issue in the repository
- Check the troubleshooting section
- Review the documentation

---

**FlashNews** - Stay informed with the latest news updates! 🚀
