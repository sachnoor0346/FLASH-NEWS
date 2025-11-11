import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.io.InputStream;

/**
 * Simple database connection test for FlashNews
 */
public class DatabaseTest {
    public static void main(String[] args) {
        try {
            // Load database properties
            Properties props = new Properties();
            InputStream input = DatabaseTest.class.getClassLoader().getResourceAsStream("database.properties");
            props.load(input);
            
            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");
            
            System.out.println("Testing database connection...");
            System.out.println("URL: " + url);
            System.out.println("Username: " + username);
            
            // Test connection
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Database connection successful!");
            
            // Test query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM categories");
            
            if (rs.next()) {
                System.out.println("✅ Categories table has " + rs.getInt("count") + " records");
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM locations");
            if (rs.next()) {
                System.out.println("✅ Locations table has " + rs.getInt("count") + " records");
            }
            
            conn.close();
            System.out.println("✅ Database test completed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Database connection failed:");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}




