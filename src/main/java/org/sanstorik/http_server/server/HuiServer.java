package org.sanstorik.http_server.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class HuiServer extends HttpServlet {
    public HuiServer() {
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().print("HELLO SUKA");

        File file = new File("hello.jpg");

        if (!file.exists()) {
            resp.getWriter().print("NOT EXISTS");
        } else {
            resp.getWriter().print(file.getAbsolutePath());
        }
    }
}
