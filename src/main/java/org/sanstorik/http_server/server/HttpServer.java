package org.sanstorik.http_server.server;

import org.sanstorik.http_server.database.ConcreteSqlConnection;
import org.sanstorik.http_server.server.queries.Query;
import org.sanstorik.neural_network.face_identifying.FaceRecognizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class HttpServer extends HttpServlet {
    private ConcreteSqlConnection databaseConnection;
    private FaceRecognizer faceRecognizer;


    @Override public void init() throws ServletException {
        //databaseConnection = new ConcreteSqlConnection();
        //faceRecognizer = FaceRecognizer.create();
    }


    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }


    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }


    private void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Query query = Query.fromRequest(req, databaseConnection);

        resp.getWriter().print(query.asJsonResponse());
    }
}