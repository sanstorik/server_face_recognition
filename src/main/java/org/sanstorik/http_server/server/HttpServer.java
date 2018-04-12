package org.sanstorik.http_server.server;

import org.apache.commons.io.FileUtils;
import org.sanstorik.http_server.database.PostgreSqlConnection;
import org.sanstorik.http_server.server.queries.Query;
import org.sanstorik.neural_network.face_detection.UserFaceDetector;
import org.sanstorik.neural_network.face_identifying.FaceRecognizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class HttpServer extends HttpServlet {
    private final PostgreSqlConnection databaseConnection = null;

    public HttpServer() {
        super();
        //this.databaseConnection = new PostgreSqlConnection();
    }


    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       /* resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "filename=\"recognizer_cache.jpg\"");
        File srcFile = org.sanstorik.neural_network.utils.FileUtils.loadFile("save_session/recognizer_cache.jpg");

        ImageIO.write(ImageIO.read(srcFile), "jpg", resp.getOutputStream()); /*/


        FaceRecognizer.create();
        UserFaceDetector.create();

        File file = new File("images/hello.jpg");
        file.createNewFile();

        FileUtils.copyURLToFile(new URL("http://lorempixel.com/400/200/"), file);
        resp.getWriter().print(createURL(req, org.sanstorik.neural_network.utils.FileUtils.getResourcesPath()
                + "images/recognizer_cache.jpg"));
    }


    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }


    private void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Query query = Query.fromRequest(req, databaseConnection);

        resp.getWriter().print(query.execute());
    }


    protected static String createURL(HttpServletRequest request, String resourcePath) {

        int port = request.getServerPort();
        StringBuilder result = new StringBuilder();
        result.append(request.getScheme())
                .append("://")
                .append(request.getServerName());

        if ( (request.getScheme().equals("http") && port != 80) || (request.getScheme().equals("https") && port != 443) ) {
            result.append(':')
                    .append(port);
        }

        result.append(request.getContextPath());

        if(resourcePath != null && resourcePath.length() > 0) {
            if( ! resourcePath.startsWith("/")) {
                result.append("/");
            }
            result.append(resourcePath);
        }

        return result.toString();
    }

}
