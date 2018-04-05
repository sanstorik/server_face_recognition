import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpServer extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //GET query to server
        if (request.getMethod().equals("GET")) {
            getQuery(request, response);
        }

        //POST query to server
        else if (request.getMethod().equals("POST")) {
            postQuery(request, response);
        }
        
        response.getWriter().print("Ilya pidoras i soset hui\n");

        String msg = request.getMethod();
        msg += request.getHeader("login");
        msg += " " + request.getHeader("password");

        response.getWriter().print("\n " + msg);
        response.getWriter().print("\n" + request.getRequestURL().toString());
        response.getWriter().print("\n" + request.getContextPath().toString());

        response.getWriter().print("\n" + request.getParameter("name"));
    }

    private void getQuery(HttpServletRequest request, HttpServletResponse response) {

    }

    private void postQuery(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().print("Ilya pidoras i soset hui!\n");
    }
}
