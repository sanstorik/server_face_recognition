package org.sanstorik.http_server.server.queries;

import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.ConcreteSqlConnection;
import org.sanstorik.neural_network.face_detection.UserFaceDetector;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;

class HighlightFacesQuery extends ProceedImageQuery {

    @Override protected BufferedImage workOnImage(HttpServletRequest request,
                                                  ConcreteSqlConnection databaseConnection, Token token, File image) {
        UserFaceDetector faceDetector = UserFaceDetector.create();
        return faceDetector.highlightFacesOnImage(image);
    }
}
