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

    public String sqlQuery() {
        String error = "";

        try {
            sqlConnection.prepareStatement("CREATE TABLE IF NOT EXISTS USERS(" +
                    "ID SERIAL PRIMARY KEY, " +
                    "NAME VARCHAR(255) NOT NULL);").execute();

            sqlConnection.prepareStatement("INSERT INTO USERS(NAME) VALUES ('MATILDA');").execute();
            return sqlConnection.prepareStatement("SELECT * FROM USERS").executeQuery().getString("name");
        } catch (SQLException e) {
            error = e.toString();
            e.printStackTrace();
        }

        return error;
    }

    private Connection getSqlConnection() throws SQLException {
        final String url = System.getenv("JDBC_DATABASE_URL");

        return DriverManager.getConnection(url);
    }
}
