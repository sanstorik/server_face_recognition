package org.sanstorik.http_server.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ConcreteSqlConnection extends DatabaseConnection {

    /**
     * To change database type - change system variable.
     * You are allowed to use postgres, mysql etc.
     */


    /**
     * Check if user is registered in database.
     * @return id of user. If user wasn't found this returns negative number.
     */
    public int checkLogin(String username, String password) {
        int id = -1;

        try {
            PreparedStatement statement = createPreparedStatement(
                    "select id, username, password from users " +
                            "where username=? and password=?;");
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet set = statement.executeQuery();

            if (set != null && set.next()) {
                id = set.getInt(id);
            }

            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }



    public boolean registerUser(String username, String password, String imageUrl) {
        try {
            PreparedStatement statement = createPreparedStatement(
                    "insert into users(username, password, image_url) " +
                            "values(?, ?, ?);");
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, imageUrl);
            return statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
