package org.sanstorik.http_server.server.queries;

import org.sanstorik.http_server.HttpResponse;
import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.ConcreteSqlConnection;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class LoginQuery extends Query {

    private Token token;


    LoginQuery() {
        super(false);
    }


    @Override protected void parseRequest(HttpServletRequest request, ConcreteSqlConnection databaseConnection) {

    }


    private String loginQuery(HttpServletRequest request, ConcreteSqlConnection databaseConnection) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if ((username == null || username.isEmpty())
                || (password == null || password.isEmpty())) {
            return HttpResponse.error("Params username and password are required.").asJson();
        }

        if (databaseConnection.checkLogin(username, password)) {
            return HttpResponse.error("Wrong password or login").asJson();
        }

        Token authToken = Token.cypherToken(username, password);

        if (authToken == null) {
            return HttpResponse.error("Couldn't create token for you.").asJson();
        }

        Map<String, String> responseParams = new HashMap<>(1);
        responseParams.put("token", authToken.getToken());

        return HttpResponse.create(responseParams).asJson();
    }
}
