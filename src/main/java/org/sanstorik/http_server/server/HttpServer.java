package org.sanstorik.http_server.server;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.sanstorik.http_server.database.PostgreSqlConnection;
import org.sanstorik.http_server.server.queries.Query;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpServer extends HttpServlet {
    private final PostgreSqlConnection databaseConnection;

    public HttpServer() {
        super();
        this.databaseConnection = new PostgreSqlConnection();
    }


    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }


    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }


    private void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Query query = Query.fromRequest(req, databaseConnection);


        resp.getWriter().print("hello world");
    }

}
