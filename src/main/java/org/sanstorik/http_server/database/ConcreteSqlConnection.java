package org.sanstorik.http_server.database;

import org.sanstorik.http_server.Token;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ConcreteSqlConnection extends DatabaseConnection {

    /** To change database type - change system variable.
     *  You are allowed to use postgres, mysql etc.
     */


    public boolean checkLogin(String username, String password) {
        try {
            PreparedStatement statement = createPreparedStatement(
                    "select id, username, password from users " +
                            "where username=? and password=?;");
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet set = statement.executeQuery();
            return set != null && set.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
