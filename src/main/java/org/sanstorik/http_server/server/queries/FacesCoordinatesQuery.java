package org.sanstorik.http_server.server.queries;

import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.ConcreteSqlConnection;
import org.sanstorik.http_server.utils.FileUtils;
import org.sanstorik.neural_network.face_detection.Face;
import org.sanstorik.neural_network.face_detection.UserFaceDetector;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

class FacesCoordinatesQuery extends Query {

    @Override protected void parseRequest(HttpServletRequest request, ConcreteSqlConnection databaseConnection, Token token) {
        Face.Response<File, String> response = readImageFromMultipartRequest(
                request, "image",
                FileUtils.getRootCachedImagesPath(),
                FileUtils.generateRandomImageName()
        );

        if (response == null) {
            errorResponse("No image given or server couldn't parse it.");
            return;
        }

        UserFaceDetector userFaceDetector = UserFaceDetector.create();
        Face[] faces = userFaceDetector.getFacesCoordinates(response.left);

        if (faces == null || faces.length == 0) {
            errorResponse("No faces found");
            return;
        }

        for (int i = 0; i < faces.length; i++) {
            double[] facesCoordinates = new double[4];
            facesCoordinates[0] = faces[i].getLeftTopX();
            facesCoordinates[1] = faces[i].getLeftTopY();
            facesCoordinates[2] = faces[i].getWidth();
            facesCoordinates[3] = faces[i].getHeight();

            addCustomArray("face_" + (i + 1), facesCoordinates);
        }
    }
}
