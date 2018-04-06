import database.PostgreSqlConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpServer extends HttpServlet {
    private enum QueryMethod {
        ACCEPT_JSON("/accept_json"), DECLINE_JSON("/decline_json"), NONE("/error");

        private String stringRepresentation;

        QueryMethod(String stringRepresentation) {
            this.stringRepresentation = stringRepresentation;
        }

        static QueryMethod of(String stringRepresentation) {
            if (stringRepresentation.equals(ACCEPT_JSON.stringRepresentation)) {
                return ACCEPT_JSON;
            } else if (stringRepresentation.equals(DECLINE_JSON.stringRepresentation)) {
                return DECLINE_JSON;
            } else {
                return NONE;
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

        String msg = request.getMethod();
        msg += request.getHeader("login");
        msg += " " + request.getHeader("password");

        response.getWriter().print("\n " + msg);
        response.getWriter().print("\n" + parseMethodByUrl(request.getRequestURL().toString()));
        response.getWriter().print("\n" + request.getContextPath().toString());

        response.getWriter().print("\n" + request.getParameter("name"));

        response.getWriter().print("\n size = " + databaseConnection.sqlQuery());
    }

    private QueryMethod parseMethodByUrl(String url) {
        String methodName = url.substring(url.lastIndexOf("/"), url.length());

        return QueryMethod.of(methodName);
    }

    private void postQuery(HttpServletRequest request, HttpServletResponse response) {

    }
}
