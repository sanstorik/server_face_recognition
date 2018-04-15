package org.sanstorik;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.sanstorik.http_server.server.HttpServer;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        try {
            startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startServer() throws Exception {
        System.out.println(System.getenv("TOKEN_KEY"));
        //create temp directory for files (heroku)
        new File("images").mkdir();

        //create main handler http
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/users");
        context.addServlet(new ServletHolder(new HttpServer()), "/api");


        server.setHandler(context);

        server.start();
        server.join();
    }
}