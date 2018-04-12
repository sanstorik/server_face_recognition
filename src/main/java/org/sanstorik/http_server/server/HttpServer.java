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

public class HttpServer extends AbstractHandler {
    private final PostgreSqlConnection databaseConnection;

    public HttpServer() {
        super();
        this.databaseConnection = new PostgreSqlConnection();
    }

    @Override public void handle(String s, Request request, HttpServletRequest httpServletRequest,
                                 HttpServletResponse httpServletResponse) throws IOException {
        Query query = Query.fromRequest(httpServletRequest, databaseConnection);

        httpServletResponse.getWriter().print("hello world");
    }
}
