package org.sanstorik.http_server.server;

import org.sanstorik.http_server.database.PostgreSqlConnection;
import org.sanstorik.http_server.server.queries.Query;
import org.sanstorik.neural_network.face_identifying.FaceRecognizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class HttpServer extends HttpServlet {
    private final PostgreSqlConnection databaseConnection = null;
    private FaceRecognizer faceRecognizer;

    public HttpServer() {
        super();
        //this.databaseConnection = new PostgreSqlConnection();
        faceRecognizer = FaceRecognizer.create();
    }


    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().print("HELLO WORLD");
    }


    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }


    private void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Query query = Query.fromRequest(req, databaseConnection);

        resp.getWriter().print(query.execute());
    }
}
