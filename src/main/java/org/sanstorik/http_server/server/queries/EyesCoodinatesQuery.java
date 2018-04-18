package org.sanstorik.http_server.server.queries;

import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.ConcreteSqlConnection;
import org.sanstorik.http_server.utils.FileUtils;
import org.sanstorik.neural_network.face_detection.Face;
import org.sanstorik.neural_network.face_detection.UserFaceDetector;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

public class EyesCoodinatesQuery extends Query {
    @Override protected void parseRequest(HttpServletRequest request, ConcreteSqlConnection databaseConnection, Token token) {
        Face.Response<File, String> response = readImageFromMultipartRequest(
                request, "image",
                FileUtils.getRootCachedImagesDirectoryName(),
                FileUtils.generateRandomImageName()
        );

        if (response.left == null) {
            errorResponse("No image given or server couldn't parse it.");
            return;
        }

        UserFaceDetector faceDetector = UserFaceDetector.create();
        List<double[]> eyes = faceDetector.getEyesCoordinates(response.left);

        if (eyes == null || eyes.isEmpty()) {
            errorResponse("Eyes couldn't be located.");
            return;
        }

        int eyes_ind = 1;
        for (double[] eyesCoord: eyes) {
            addCustomArray("eyes_" + eyes_ind, eyesCoord);
            eyes_ind++;
        }
    }
}
