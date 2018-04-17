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
import java.util.UUID;

import static org.sanstorik.neural_network.face_identifying.FaceRecognizer.Prediction;

class LoginPhotoQuery extends FaceFeatureQuery {

    LoginPhotoQuery() {
        super(false);
    }


    @Override protected void parseRequest(HttpServletRequest request, ConcreteSqlConnection databaseConnection, Token token) {
        Face.Response<File, String> response = readImageFromMultipartRequest(request, "image",
                FileUtils.getRootCachedImagesDirectoryName(), FileUtils.generateRandomImageName());

        if (response.left == null) {
            errorResponse("No image given.");
            return;
        }

        FaceRecognizer recognizer = FaceRecognizer.create();

        FaceFeatures[] featuresOfAllUsers = getFeaturesOfAllUsers(databaseConnection);
        if (featuresOfAllUsers == null) {
            errorResponse("Couldn't match users from database with a photo.");
            return;
        }

        Prediction prediction = recognizer.identifyUserFromFeaturePool(response.left, featuresOfAllUsers);

        if (prediction == null) {
            errorResponse("User photo couldn't be proceeded. Make sure a face on the photo is valid.");
            return;
        }

        addParam("max_probability", String.valueOf(prediction.getPercentage()));
        addParam("matched", String.valueOf(prediction.isIdentificated()));

        if (prediction.isIdentificated()) {
            long foundMatchedUserId = prediction.getActualFeatures().getIdentifier();

            User foundUser = databaseConnection.getUserById((int) foundMatchedUserId);

            Token cypheredToken = Token.cypherToken(foundUser.getUsername(),
                    foundUser.getPassword(), foundUser.getUserId());

            if (cypheredToken != null) {
                addParam("Authorization", cypheredToken.getToken());
                addParam("username", foundUser.getUsername());
            } else {
                errorResponse("User was matched but server couldn't create token by some reason.");
            }
        }
    }
}
