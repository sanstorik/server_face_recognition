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

class RegisterQuery extends JsonFeatureQuery {

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
        String jsonUrl = FileUtils.getRootJsonPath() + directoryUserJson + "/" + jsonFileName;

        boolean createdJson = imagePair.left != null
                && createJsonWithFaceFeatures(jsonUrl, imagePair.left, directoryUserJson, username);

        if (!createdJson || !databaseConnection.registerUser(username, password, imagePair.right, jsonUrl)) {
            errorResponse("Couldn't create user or some files or find face on image.");
        }
    }
}
