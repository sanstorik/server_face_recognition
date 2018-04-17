package org.sanstorik.http_server.server.queries;

import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.ConcreteSqlConnection;
import org.sanstorik.http_server.database.User;
import org.sanstorik.http_server.utils.FileUtils;
import org.sanstorik.neural_network.face_detection.Face;
import org.sanstorik.neural_network.face_identifying.FaceFeatures;
import org.sanstorik.neural_network.face_identifying.FaceRecognizer;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

class UpdateUserPhotoQuery extends JsonFeatureQuery {


    @Override protected void parseRequest(HttpServletRequest request, ConcreteSqlConnection databaseConnection, Token token) {
        String newImageName = FileUtils.generateRandomImageName();

        Face.Response<File, String> response = readImageFromMultipartRequest(request, "image",
                FileUtils.generateRandomString(), newImageName);

        if (response.left == null) {
            errorResponse("Couln't get new image.");
            return;
        }

        String jsonDirectory = FileUtils.generateRandomString();
        String jsonPath = FileUtils.getRootJsonPath() + jsonDirectory +
                "/" + FileUtils.generateRandomString() + ".json";

        if (response.left == null
                || !createJsonWithFaceFeatures(jsonPath, response.left, jsonDirectory, token.getUsername())
                || !databaseConnection.update(token.getUserId(), response.right, jsonPath)) {
                    errorResponse("Update failed. Couldn't find a face on image or update a file.");
        }
    }
}
