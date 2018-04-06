import fi.iki.elonen.NanoHTTPD;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Server extends NanoHTTPD
{

    public Server() throws IOException {
        super(5000);

        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");
    }

    @Override public Response serve(IHTTPSession session) {

        //METHOD accept_json
        if (session.getUri().endsWith("accept_json")) {
            String msg = "hello ";

            msg += session.getHeaders().get("login");
            msg += " " + session.getMethod().toString();

            return newFixedLengthResponse(msg);
        }

        //METHOD decline_json
        else if (session.getUri().endsWith("decline_json")) {
            return newFixedLengthResponse("decline json");
        }

        //POST-METHOD post_image - upload image
        else if(session.getUri().endsWith("post_image")) {
            Map<String, String> body = new HashMap<String, String>();
            body.put("one", "two");

            try {
                session.parseBody(body);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(body.get("image"));
            File file = new File(body.get("image"));

            try {
                File newImage = new File("/Users/chloe/Downloads/server/target/classes/my_image.jpg");
                newImage.createNewFile();

                FileUtils.copyFileUsingChannel(file, newImage);
                System.out.println(newImage.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return newFixedLengthResponse("code: " + Response.Status.OK.toString() + " and body: " + body.toString());
        }

        return null;
    }
}
