package org.sanstorik.http_server.server.queries;

import org.apache.http.HttpRequest;
import org.sanstorik.http_server.HttpResponse;

import javax.servlet.http.HttpServletRequest;

public abstract class Query {
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
    private Type type;
    private HttpServletRequest request;
    private final HttpResponse response = HttpResponse.fromTemplate();

    Query() { }


    Query(Type type) { }


    /**
     * Parse url and data from request and create appropriate class
     * to handle it.
     * @param request from user
     * @return query with needed response
     */
    public static Query fromRequest(HttpServletRequest request) {
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

        if (query.type != Type.of(request.getMethod()) ||
                query.type != Type.UNIQUE) {
            query = new NotSupportedQueryType();
        }


        return query;
    }


    public final String asJson() {
        return response.asJson();
    }


    final HttpServletRequest getRequest() {
        return request;
    }
}