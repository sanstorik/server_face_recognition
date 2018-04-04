import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.*;

public class Main extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.getWriter().print("Илюха пидарас сосет хуй!\n");

        String msg = req.getMethod();
        msg += req.getHeader("login");
        msg += " " +req.getHeader("password");

        resp.getWriter().print("\n " + msg);
        resp.getWriter().print("\n" + req.getRequestURL().toString());
        resp.getWriter().print("\n" + req.getContextPath().toString());

        resp.getWriter().print("\n" + req.getParameter("name"));
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().print("Илюха пидарас сосет хуй!\n");
    }


    public static void main(String[] args) throws Exception{
        Server server = new Server(Integer.valueOf(System.getenv("PORT")));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new Main()),"/*");
        server.start();
        server.join();
    }
}