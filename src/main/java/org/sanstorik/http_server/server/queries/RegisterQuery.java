package org.sanstorik.http_server.server.queries;

import javafx.util.Pair;
import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.ConcreteSqlConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;

public class RegisterQuery extends Query {


    RegisterQuery() {
        super(false);
    }


    @Override protected void parseRequest(HttpServletRequest request, ConcreteSqlConnection databaseConnection, Token token) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Pair<File, String> imagePair = readImageFromMultipartRequest(request, "image", username);

        if (username == null || password == null || imagePair.getKey() == null) {
            errorResponse("Username, password or image params are missing.");
        } else if (!databaseConnection.registerUser(username, password, imagePair.getValue())) {
            errorResponse("Couln't create user.");
        }

        System.out.println(imagePair.getValue());
    }
}
