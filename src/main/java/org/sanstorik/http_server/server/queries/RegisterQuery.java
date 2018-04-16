package org.sanstorik.http_server.server.queries;

import com.google.gson.Gson;
import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.ConcreteSqlConnection;
import org.sanstorik.http_server.utils.FileUtils;
import org.sanstorik.neural_network.face_detection.Face;
import org.sanstorik.neural_network.face_identifying.FaceFeatures;
import org.sanstorik.neural_network.face_identifying.FaceRecognizer;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

class RegisterQuery extends Query {

    RegisterQuery() {
        super(false);
    }


    @Override protected void parseRequest(HttpServletRequest request, ConcreteSqlConnection databaseConnection, Token token) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null) {
            errorResponse("Username, password or image params are missing.");
            return;
        } else if (databaseConnection.isRegistered(username)) {
            errorResponse("That login is allready taken.");
            return;
        }

        Face.Response<File, String> imagePair = readImageFromMultipartRequest(request, "image",
                FileUtils.generateRandomString(), FileUtils.generateRandomImageName());

        String directoryUserJson = FileUtils.generateRandomString();
        String jsonFileName = FileUtils.generateRandomString() + ".json";
        String jsonUrl = FileUtils.getRootJsonPath() + directoryUserJson + jsonFileName;

        boolean createdJson = createJsonWithFaceFeatures(jsonUrl, imagePair.left, directoryUserJson, username);

        if (imagePair.left != null && createdJson
                && !databaseConnection.registerUser(username, password, imagePair.right, jsonUrl)) {
            errorResponse("Couldn't create user or some files or find face on image.");
        }
    }


    private boolean createJsonWithFaceFeatures(String jsonPath, File image, String directory, String username) {
        File json = new File(jsonPath);

        //create full path dirs
        try {
            new File(FileUtils.getRootJsonPath() + directory).mkdirs();

            if (!json.exists()) {
                json.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        FaceRecognizer faceRecognizer = FaceRecognizer.create();
        FaceFeatures features = faceRecognizer.calculateFeaturesForFace(image, username);

        //no faces found on a picture
        if (features == null) {
            return false;
        }

        boolean isWritten = false;
        try (Writer writer = new FileWriter(json)) {
            Gson gson = new Gson();
            gson.toJson(features, writer);
            isWritten = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isWritten;
    }
}
