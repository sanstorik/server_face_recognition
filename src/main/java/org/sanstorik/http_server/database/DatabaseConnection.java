package org.sanstorik.http_server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

abstract class DatabaseConnection {

    private Connection sqlConnection;
    private final String DATABASE_URL;

    DatabaseConnection(String url) {
        DATABASE_URL = url;

        try {
            sqlConnection = getSqlConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean closeConnection() {
        boolean closed = false;

        try {
            if (!sqlConnection.isClosed()) {
                sqlConnection.close();
                sqlConnection = null;
                closed = true;
            }
        } catch (SQLException e) { /* empty */ }

        return closed;
    }


    public boolean refreshConnection() {
        boolean refreshed = false;

        try {
            if (sqlConnection.isClosed()) {
                refreshed = true;
                sqlConnection = getSqlConnection();
            }
        } catch (SQLException e) { /* empty */}

        return refreshed;
    }


    protected boolean executeSql(String sql) {
        boolean executed = false;
        try {
            executed = sqlConnection.prepareStatement(sql).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return executed;
    }


    protected ResultSet executeSqlQuery(String sql) {
        ResultSet resultSet = null;

        try {
            resultSet = sqlConnection.prepareStatement(sql).executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSet;
    }


    /**
     * Establish connection to a PostgreSQL database.
     * As database url we use System independent environment variable $JDBC_DATABASE_URL
     * which looks like this < jdbc:postgresql://host:port/database_name >.
     * Add ssl, password and username if needed.
     * @return connection to PostgreSQL database.
     */
    private Connection getSqlConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }
}
