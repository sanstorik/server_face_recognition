package org.sanstorik.http_server.server;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.sanstorik.http_server.HttpResponse;
import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.PostgreSqlConnection;
import org.sanstorik.http_server.server.queries.Query;

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
    private final PostgreSqlConnection databaseConnection;

    public HttpServer() {
        super();
        this.databaseConnection = new PostgreSqlConnection();
    }


    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Query query = Query.fromRequest(request, databaseConnection);

        response.getWriter().print(query.asJson());
    }


    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Query query = Query.fromRequest(request, databaseConnection);

        response.getWriter().print(query.asJson());
    }
}
