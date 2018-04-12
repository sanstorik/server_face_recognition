package org.sanstorik.http_server.server.queries;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.sanstorik.http_server.HttpResponse;
import org.sanstorik.http_server.Token;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginQuery extends Query {

    private Token token;

    @Override protected void parseRequest(HttpServletRequest request) {

    }

    private String loginQuery(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if ((username == null || username.isEmpty())
                || (password == null || password.isEmpty())) {
            return HttpResponse.error("Params username and password are required.").asJson();
        }

        if (!getDatabaseConnection().checkLogin(username, password)) {
            return HttpResponse.error("Wrong password or login").asJson();
        }

        Token authToken = createToken(username, password);

        if (authToken == null) {
            return HttpResponse.error("Couldn't create token for you.").asJson();
        }

        Map<String, String> responseParams = new HashMap<>(1);
        responseParams.put("token", authToken.getToken());

        return HttpResponse.create(responseParams).asJson();
    }


    private Token createToken(String username, String password) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("sanstorik_mangix");
            String token = JWT.create()
                    .withClaim("username", username)
                    .withClaim("password", password)
                    .withExpiresAt(new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000))
                    .sign(algorithm);

            return new Token(token, username, password);
        } catch (UnsupportedEncodingException |JWTCreationException e) {
            e.printStackTrace();
        }

        return null;
    }
}
