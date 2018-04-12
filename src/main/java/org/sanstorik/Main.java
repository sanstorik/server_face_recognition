package org.sanstorik;

import org.bytedeco.javacpp.opencv_core;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.sanstorik.http_server.server.HttpServer;

public class Main {

    public static void main(String[] args) {
        try {
            opencv_core.Mat mat = new opencv_core.Mat();
            mat.t();
            startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startServer() throws Exception {
        Server server = new Server(5000);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new HttpServer()), "/*");

        server.start();
        server.join();
    }
}