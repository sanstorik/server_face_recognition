package org.sanstorik.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgreSqlConnection extends DatabaseConnection {

    public PostgreSqlConnection() {
        super(System.getenv("JDBC_DATABASE_URL"));
    }

    public String sqlQuery() {
        executeSql("INSERT INTO USERS(NAME, PASSWORD) VALUES ('MATILDA', 'USER');");

        String error = "";
        try {
            ResultSet set = executeSqlQuery("SELECT * FROM USERS;");
            set.next();

            return set.getString("NAME");
        } catch (SQLException e) {
            e.printStackTrace();
            error = e.toString();
        }

        return error;
    }
}
