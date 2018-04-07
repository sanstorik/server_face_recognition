package org.sanstorik.server;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.sanstorik.database.PostgreSqlConnection;
import org.sanstorik.database.Token;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HttpServer extends HttpServlet {
    @FunctionalInterface
    private interface QueryExecutor {
        String processQuery();
    }
    private enum QueryMethod {
        LOGIN("/login"), ACCEPT_JSON("/accept_json"), DECLINE_JSON("/decline_json"),
        UPLOAD_IMAGE("/upload_image"), NOT_SUPPORTED("/not_supported");

        private String stringRepresentation;

        QueryMethod(String stringRepresentation) {
            this.stringRepresentation = stringRepresentation;
        }

        static QueryMethod of(String stringRepresentation) {
            if (stringRepresentation.equals(ACCEPT_JSON.stringRepresentation)) {
                return ACCEPT_JSON;
            } else if (stringRepresentation.equals(LOGIN.stringRepresentation)) {
                return LOGIN;
            } else if (stringRepresentation.equals(DECLINE_JSON.stringRepresentation)) {
                return DECLINE_JSON;
            } else if (stringRepresentation.equals(UPLOAD_IMAGE.stringRepresentation)) {
                return UPLOAD_IMAGE;
            } else {
                return NOT_SUPPORTED;
            }
        }

        @Override  public String toString() {
            return stringRepresentation;
        }
    }

    private final PostgreSqlConnection databaseConnection;

    public HttpServer() {
        super();

        this.databaseConnection = new PostgreSqlConnection();
    }


    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getQuery(request, response);
    }


    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        postQuery(request, response);
    }


    private void getQuery(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        QueryMethod queryMethod = parseMethodByUrl(request.getRequestURL().toString());
        String jsonResponse = "";

        switch (queryMethod) {
            case LOGIN: {
                jsonResponse = loginQuery(request);
                break;
            }
            case ACCEPT_JSON: {
                jsonResponse = acceptJsonQuery(request);
                break;
            }
            case DECLINE_JSON: {
                jsonResponse = declineJsonQuery();
                break;
            }
            case NOT_SUPPORTED: {
                jsonResponse = errorQuery();
                break;
            }
        }

        response.getWriter().print(jsonResponse);
    }


    private void postQuery(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        QueryMethod queryMethod = parseMethodByUrl(request.getRequestURL().toString());
        String jsonResponse = "";

        switch (queryMethod) {
            case UPLOAD_IMAGE: {
                jsonResponse = uploadImageQuery(request);
                break;
            }
            case NOT_SUPPORTED: {
                jsonResponse = errorQuery();
                break;
            }
        }

        response.getWriter().print(jsonResponse);
    }


    private String acceptJsonQuery(HttpServletRequest request) {
        return executeAfterTokenIsVerified(request, "No json available.",
                () -> HttpResponse.create(null).asJson());
    }


    private String declineJsonQuery() {
        return "";
    }


    private String errorQuery() {
        return HttpResponse.error("Not supported method.").asJson();
    }


    private String uploadImageQuery(HttpServletRequest request) {
        return executeAfterTokenIsVerified(request,
                "Faced problem during upload. Check your token.", () -> {
                    Map<String, String> params = new HashMap<>();
                    params.put("image", "image");
                    return HttpResponse.create(params).asJson();
                });
    }


    /**
     * Method is used to abstract from checking token validation in query.
     * @param request Rest API request from user
     * @param errorMessage message to user if we couldn't parse token
     * @param executor normal functionality that returns valid json to user.
     * @return json API response to user
     */
    private String executeAfterTokenIsVerified(HttpServletRequest request, String errorMessage,
                                               QueryExecutor executor) {
        //token is in form Bearer + <token>
        String token = request.getHeader("Authorization");
        if (token == null || token.length() < 8) {
            return HttpResponse.error("Token is empty or too short.").asJson();
        }

        token = token.substring(7);
        try {
            Algorithm algorithm = Algorithm.HMAC256("sanstorik_mangix");

            DecodedJWT decoded = JWT.require(algorithm)
                    .build().verify(token);

            if (decoded.getExpiresAt().before(new Date())) {
                return HttpResponse.error("Token usability time has been expired. Create a new one.").asJson();
            }

            if (!databaseConnection.isValidToken(
                    new Token(token, decoded.getClaim("username").asString(),
                            decoded.getClaim("password").asString()))) {
                return HttpResponse.error("Token is not verified. Invalid user.").asJson();
            }

            return executor.processQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return HttpResponse.error(errorMessage).asJson();
    }


    private String loginQuery(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if ((username == null || username.isEmpty())
                || (password == null || password.isEmpty())) {
            return HttpResponse.error("Params username and password are required.").asJson();
        }

        if (!databaseConnection.checkLogin(username, password)) {
            return HttpResponse.error("Wrong password or login").asJson();
        }

        String authToken = createToken(username, password);

        if (authToken == null) {
            return HttpResponse.error("Couldn't create token for you.").asJson();
        }

        Map<String, String> responseParams = new HashMap<>(1);
        responseParams.put("token", authToken);

        return HttpResponse.create(responseParams).asJson();
    }


    private String createToken(String username, String password) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("sanstorik_mangix");
            String token = JWT.create()
                    .withClaim("username", username)
                    .withClaim("password", password)
                    .withExpiresAt(new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000))
                    .sign(algorithm);

            //insert token to database
            databaseConnection.putToken(new Token(token, username, password));

            return token;
        } catch (UnsupportedEncodingException|JWTCreationException e) {
            e.printStackTrace();
        }

        return null;
    }


    private QueryMethod parseMethodByUrl(String url) {
        String methodName = url.substring(url.lastIndexOf("/"), url.length());

        return QueryMethod.of(methodName);
    }
}
