import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private Connection sqlConnection;

    DatabaseConnection() {
        try {
            sqlConnection = getSqlConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int sqlQuery() {
        try {
            sqlConnection.prepareStatement("CREATE TABLE IF NOT EXISTS USERS(" +
                    "ID INT PRIMARY KEY  NOT NULL, " +
                    "NAME VARCHAR(255) NOT NULL);").execute();

            sqlConnection.prepareStatement("INSERT INTO USERS(NAME) VALUES ('MATILDA');").execute();
            return sqlConnection.prepareStatement("SELECT * FROM USERS").executeQuery().getFetchSize();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private Connection getSqlConnection() throws SQLException {
        final String url = System.getenv("DATABASE_URL");
        final String username = System.getenv("DATABASE_USERNAME");
        final String password = System.getenv("DATABASE_PASSWORD");

        return DriverManager.getConnection(url, username, password);
    }
}
