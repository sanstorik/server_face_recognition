package org.sanstorik.http_server.server.queries;

import org.sanstorik.http_server.HttpResponse;
import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.ConcreteSqlConnection;

import javax.servlet.http.HttpServletRequest;

public abstract class Query {
    @FunctionalInterface
    public interface QueryExecutor {
        String processQuery();
    }

    public enum Type {
        GET, POST, PUT, DELETE, UNIQUE;

        public static Type of(String type) {
            switch (type) {
                case "GET":
                    return Type.GET;
                case "POST":
                    return Type.POST;
                case "PUT":
                    return Type.PUT;
                case "DELETE":
                    return Type.DELETE;
                case "UNIQUE":
                    return Type.UNIQUE;
                default:
                    throw new IllegalArgumentException("No such type.");
            }
        }
    }

    private String queryMethod;
    private Type type = Type.POST;
    private boolean doCheckAuth = true;
    private HttpServletRequest request;
    private final HttpResponse response = HttpResponse.fromTemplate();
    private ConcreteSqlConnection databaseConnection;


    Query() { }


    Query(Type type) {
        this.type = type;
    }


    Query(boolean doCheckAuth) {
        this.doCheckAuth = doCheckAuth;
    }


    /**
     * Parse url and data from request and create appropriate class
     * to handle it.
     *
     * @param request from user
     * @return query with needed response
     */
    public static Query fromRequest(HttpServletRequest request, ConcreteSqlConnection databaseConnection) {
        String url = request.getRequestURL().toString();
        String method = url.substring(url.lastIndexOf("/"), url.length());

        Query query;

        switch (method) {
            case "/register":
                query = new RegisterQuery();
                break;
            case "/login":
                query = new LoginQuery();
                break;
            case "/login_photo":
                query = new LoginPhotoQuery();
                break;
            case "/highlight_faces":
                query = new HighlightFacesQuery();
                break;
            case "/faces_coordinates":
                query = new FacesCoordinatesQuery();
                break;
            case "/identify_group":
                query = new IdentifyGroupQuery();
                break;
            case "/update_user_photo":
                query = new UpdateUserPhotoQuery();
                break;
            default:
                query = new NotSupportedQuery();
                break;
        }

        if (query.type != Type.of(request.getMethod()) &&
                query.type != Type.UNIQUE) {
            query = new NotSupportedQueryType();
        }


        query.request = request;
        query.queryMethod = method;
        query.databaseConnection = databaseConnection;


        System.out.println("Query = " + query.getClass().toString());
        query.parseRequest(request, databaseConnection);

        return query;
    }


    public final String asJsonResponse() {
        return doCheckAuth ? executeAfterTokenIsVerified() : execute();
    }


    /**
     * Main method that checks input and is making a response.
     * Override this to proccess specific query.
     *
     * @param request
     * @param databaseConnection connection to database
     */
    protected abstract void parseRequest(HttpServletRequest request, ConcreteSqlConnection databaseConnection);


    protected void errorResponse(String message) {
        response.setErrorMessage(message);
        response.setStatusError();
    }


    /**
     * Method is used to abstract from checking token validation in query.
     *
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

        Token decypheredToken = Token.decypherToken(token);
        boolean isValidToken = true;

        if (decypheredToken == null) {
            errorMessage = "Token is not verified. Invalid user. Make sure you've put Bearer in front.";
            isValidToken = false;
        } else if (decypheredToken.isExpired()) {
            errorMessage = "Token usability time has been expired. Create a new one.";
            isValidToken = false;
        }

        return isValidToken ? execute() : HttpResponse.error(errorMessage).asJson();
    }


    private String execute() {
        return response.asJson();
    }
}