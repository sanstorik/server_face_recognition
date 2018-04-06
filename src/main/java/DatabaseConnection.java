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
            sqlConnection.prepareStatement("DROP TABLE IF EXISTS USERS;").execute();

            sqlConnection.prepareStatement("CREATE TABLE IF NOT EXISTS USERS(" +
                    "ID SERIAL PRIMARY KEY, " +
                    "PASSWORD VARCHAR(255) NOT NULL, " +
                    "NAME VARCHAR(255) NOT NULL);").execute();

            sqlConnection.prepareStatement("INSERT INTO USERS(NAME, PASSWORD) VALUES ('MATILDA', 'USER');").execute();
            return String.valueOf(sqlConnection.prepareStatement("SELECT * FROM USERS;").executeQuery().next());
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
