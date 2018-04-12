package org.sanstorik.http_server.server.queries;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.http.HttpRequest;
import org.sanstorik.http_server.HttpResponse;
import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.PostgreSqlConnection;
import org.sanstorik.http_server.server.HttpServer;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public abstract class Query {
    @FunctionalInterface
    public interface QueryExecutor {
        String processQuery();
    }

    public enum Type {
        GET, POST, PUT, DELETE, UNIQUE;

        public static Type of(String type) {
            switch (type) {
                case "GET": return Type.GET;
                case "POST": return Type.POST;
                case "PUT": return Type.PUT;
                case "DELETE": return Type.DELETE;
                case "UNIQUE": return Type.UNIQUE;
                default: throw new IllegalArgumentException("No such type.");
            }
        }
    }

    private String queryMethod;
    private Type type = Type.POST;
    private HttpServletRequest request;
    private final HttpResponse response = HttpResponse.fromTemplate();
    private PostgreSqlConnection databaseConnection;


    Query() {
        parseRequest(request);
    }


    Query(Type type) {
        this.type = type;
        parseRequest(request);
    }

    /**
     * Parse url and data from request and create appropriate class
     * to handle it.
     * @param request from user
     * @return query with needed response
     */
    public static Query fromRequest(HttpServletRequest request, PostgreSqlConnection databaseConnection) {
        String url = request.getRequestURL().toString();
        String method = url.substring(url.lastIndexOf("/"), url.length());

        Query query;

        switch (method) {
            case "/register": query = new RegisterQuery(); break;
            case "/login": query = new LoginQuery(); break;
            case "/login_photo": query = new LoginPhotoQuery(); break;
            case "/highlight_faces": query = new HighlightFacesQuery(); break;
            case "/faces_coordinated": query = new FacesCoordinatesQuery(); break;
            case "/identify_group": query = new IdentifyGroupQuery(); break;
            case "/update_user_photo": query = new UpdateUserPhotoQuery(); break;
            default: query = new NotSupportedQuery(); break;
        }

        query.queryMethod = method;
        query.request = request;
        query.databaseConnection = databaseConnection;

        if (query.type != Type.of(request.getMethod()) ||
                query.type != Type.UNIQUE) {
            query = new NotSupportedQueryType();
        }


        return query;
    }


    public final String execute() {
        return executeAfterTokenIsVerified();
    }


    protected abstract void parseRequest(HttpServletRequest request);


    PostgreSqlConnection getDatabaseConnection() {
        return databaseConnection;
    }


    /**
     * Method is used to abstract from checking token validation in query.
     * @return json API response to user
     */
    private String executeAfterTokenIsVerified() {
        //token is in form {Bearer <token>}
        String token = request.getHeader("Authorization");
        if (token == null || token.length() < 8) {
            return HttpResponse.error("Token is empty or too short.").asJson();
        }

        String errorMessage = "Server wasn't able to verificate token";
        token = token.substring(7);

        try {
            Token decypheredToken = Token.decypherToken(token);
            boolean isValidToken = true;

            if (decypheredToken.isExpired()) {
                errorMessage = "Token usability time has been expired. Create a new one.";
                isValidToken = false;
            }

            if (!databaseConnection.isValidToken(decypheredToken)) {
                errorMessage = "Token is not verified. Invalid user. Make sure you've put Bearer in front.";
                isValidToken = false;
            }

            return isValidToken? response.asJson() : HttpResponse.error(errorMessage).asJson();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return HttpResponse.error(errorMessage).asJson();
    }
}