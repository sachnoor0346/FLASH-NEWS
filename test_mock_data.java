import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.io.InputStream;

/**
 * Test script to verify mock data is working
 */
public class test_mock_data {
    public static void main(String[] args) {
        try {
            // Load database properties
            Properties props = new Properties();
            InputStream input = test_mock_data.class.getClassLoader().getResourceAsStream("database.properties");
            props.load(input);
            
            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");
            
            System.out.println("Testing FlashNews Mock Data...");
            System.out.println("URL: " + url);
            
            // Test connection
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Database connection successful!");
            
            // Check categories
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM categories");
            if (rs.next()) {
                System.out.println("✅ Categories: " + rs.getInt("count") + " records");
            }
            
            // Check locations
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM locations");
            if (rs.next()) {
                System.out.println("✅ Locations: " + rs.getInt("count") + " records");
            }
            
            // Check news articles
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM news_articles");
            if (rs.next()) {
                System.out.println("✅ News Articles: " + rs.getInt("count") + " records");
            }
            
            // Show sample articles
            rs = stmt.executeQuery("SELECT title, source_name, published_at FROM news_articles ORDER BY published_at DESC LIMIT 5");
            System.out.println("\n📰 Sample News Articles:");
            while (rs.next()) {
                System.out.println("- " + rs.getString("title") + " (" + rs.getString("source_name") + ")");
            }
            
            conn.close();
            System.out.println("\n🎉 Mock data test completed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed:");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
