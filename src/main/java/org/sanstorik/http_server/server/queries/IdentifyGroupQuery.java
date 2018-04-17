package org.sanstorik.http_server.server.queries;

import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.ConcreteSqlConnection;
import org.sanstorik.neural_network.face_identifying.FaceFeatures;
import org.sanstorik.neural_network.face_identifying.FaceRecognizer;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;

class IdentifyGroupQuery extends ProceedImageQuery {

    @Override protected BufferedImage workOnImage(HttpServletRequest request,
                                                  ConcreteSqlConnection databaseConnection, Token token, File image) {
        FaceRecognizer faceRecognizer = FaceRecognizer.create();
        FaceFeatures[] features = getFeaturesOfAllUsers(databaseConnection);

        return features == null ? null : faceRecognizer.identifyUsersOnPhoto(image, features);
    }
}
