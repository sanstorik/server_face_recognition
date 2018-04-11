package org.sanstorik.http_server.database;

import org.sanstorik.http_server.Token;

import java.sql.ResultSet;
import java.sql.SQLException;


public class PostgreSqlConnection extends DatabaseConnection {
    private static final String USERS_TABLE_NAME = "users";

    public PostgreSqlConnection() {
        super("jdbc:postgresql://localhost:5432/chloe");
    }


    public boolean checkLogin(String username, String password) {
        ResultSet set = selectUserQuery(username, password);

        try {
            return set != null && set.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    public void putToken(Token token) {
        executeSql("insert into tokens(token, username, password) values(" +
                "'"+ token.getToken() + "'," +
                "'" + token.getUsername()+"'," +
                "'" + token.getPassword() +"');"
        );
    }


    public boolean isValidToken(Token token) {
        ResultSet set = executeSqlQuery("select id, token, username, password from tokens " +
                "where token='"+ token.getToken() + "';");

        boolean isValid = false;

        try {
            isValid = set != null && set.next()
                    && set.getString("username").equals(token.getUsername())
                    && set.getString("password").equals(token.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isValid;
    }


    private ResultSet selectUserQuery(String username, String password) {
        ResultSet set = executeSqlQuery("select id, username, password from " + USERS_TABLE_NAME +
                " where username='"+ username +"' and password='"+ password +"';");

        return set;
    }
}
