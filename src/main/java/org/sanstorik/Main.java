package org.sanstorik;

import com.sun.corba.se.spi.activation.ServerHolder;
import org.bytedeco.javacpp.opencv_core;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.sanstorik.http_server.server.HttpServer;
import org.sanstorik.neural_network.face_identifying.FaceRecognizer;
import org.sanstorik.neural_network.utils.FileUtils;

import java.io.File;
import java.net.URL;

public class Main {

    public static void main(String[] args) {
        try {
            startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startServer() throws Exception {
        //Integer.valueOf(System.getenv("PORT"))

        final String resourcePath = "images";


        //create main handler http
        Server server = new Server(5000);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/users");
        context.setResourceBase(resourcePath);
        context.addServlet(new ServletHolder(new HttpServer()), "/api");



        //resources initialization
        ServletContextHandler secondHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        secondHandler.setContextPath("/images");
        secondHandler.setResourceBase(resourcePath);

        ServletHolder servletHolder = new ServletHolder("static-home", DefaultServlet.class);
        servletHolder.setInitParameter("resourceBase", resourcePath);
        servletHolder.setInitParameter("dirAllowed", "false");
        servletHolder.setInitParameter("pathInfoOnly","true");
        secondHandler.addServlet(servletHolder,"/foo/*");

        String defName = "default";
        ServletHolder defaultHolder = new ServletHolder(defName, DefaultServlet.class);
        defaultHolder.setInitParameter("dirAllowed", "false");

        secondHandler.addServlet(defaultHolder, "/");


        //register and start
        HandlerList handlerList = new HandlerList(context, secondHandler);
        server.setHandler(handlerList);

        server.start();
        server.join();
    }
}