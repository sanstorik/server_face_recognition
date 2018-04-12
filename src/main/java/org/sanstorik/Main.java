package org.sanstorik;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.sanstorik.http_server.server.HttpServer;

public class Main {

    public static void main(String[] args) {
        try {
            startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startServer() throws Exception {
        Server server = new Server(5000);
        server.setHandler(new HttpServer());

        server.start();
        server.join();
    }
}