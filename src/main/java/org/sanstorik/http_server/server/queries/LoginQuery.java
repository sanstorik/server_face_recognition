package org.sanstorik.http_server.server.queries;

import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.ConcreteSqlConnection;

import javax.servlet.http.HttpServletRequest;

public class LoginQuery extends Query {

    private Token token;


    LoginQuery() {
        super(Type.GET, false);
    }


    @Override protected void parseRequest(HttpServletRequest request, ConcreteSqlConnection databaseConnection, Token token) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.isEmpty()
                || password == null || password.isEmpty()) {
            errorResponse("Params username and password are missing.");
            return;
        }

        int usedId = databaseConnection.checkLogin(username, password);
        if (usedId < 0) {
            errorResponse("Login is not verified.");
            return;
        }

        Token authToken = Token.cypherToken(username, password, usedId);
        if (authToken == null) {
            errorResponse("Token couldn't be created.");
            return;
        }


        addParam("Authorization", authToken.getToken());
    }
}
