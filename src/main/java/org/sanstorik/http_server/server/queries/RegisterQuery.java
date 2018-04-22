package org.sanstorik.http_server.server.queries;

import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.ConcreteSqlConnection;
import org.sanstorik.http_server.utils.FileUtils;
import org.sanstorik.neural_network.face_detection.BufferedFace;
import org.sanstorik.neural_network.face_detection.Face;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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


        List<File> images = new ArrayList<>();
        String randomImageUrl = "";

        for (int imageKeyIndex = 1; imageKeyIndex <= 5; imageKeyIndex++) {
            Face.Response<File, String> imagePair = readImageFromMultipartRequest(request, "image" + imageKeyIndex,
                    FileUtils.generateRandomString(), FileUtils.generateRandomImageName());

            if (imagePair.left != null) {
                randomImageUrl = imagePair.right;
            }
        }

        if (images.isEmpty() || images.size() < 3) {
            errorResponse("Not enough images given. Should be at least 3 for (left;center;right).;");
            return;
        }

        String directoryUserJson = FileUtils.generateRandomString();
        String jsonFileName = FileUtils.generateRandomString() + ".json";
        String jsonUrl = FileUtils.getRootJsonPath() + directoryUserJson + "/" + jsonFileName;

        File[] fileImages = images.toArray(new File[images.size()]);

        if (!createJsonWithFaceFeatures(jsonUrl, directoryUserJson, username, fileImages)) {
            errorResponse("Couldn't create user. Maybe not enough images are given. " +
                    "You should know that there should always be photos from each side " +
                    ": where you face right, left and at camera.");
        } else if (!databaseConnection.registerUser(username, password, randomImageUrl, jsonUrl)) {
            errorResponse("Couldn't register user in database.");
        }
    }
}
