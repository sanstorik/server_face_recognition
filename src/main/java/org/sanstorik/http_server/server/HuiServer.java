package org.sanstorik.http_server.server;

import org.sanstorik.neural_network.face_detection.UserFaceDetector;
import org.sanstorik.neural_network.face_identifying.FaceRecognizer;

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
        file.createNewFile();

        resp.getWriter().print(file.getAbsolutePath());

        long heapSize = Runtime.getRuntime().totalMemory();

// Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
        long heapMaxSize = Runtime.getRuntime().maxMemory();

        // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
        long heapFreeSize = Runtime.getRuntime().freeMemory();

        resp.getWriter().print(
                "Heapsize = " + heapSize + "heap size max = " + heapMaxSize + " free = " + heapFreeSize );

        FaceRecognizer.create();

        resp.getWriter().print("\n\nHeapsize = " + Runtime.getRuntime().totalMemory() +
                "heap size max = " + Runtime.getRuntime().maxMemory() +
                " free = " + Runtime.getRuntime().freeMemory() );

        if (!file.exists()) {
            resp.getWriter().print("NOT EXISTS");
        } else {
            resp.getWriter().print(file.getAbsolutePath());
        }
    }
}
