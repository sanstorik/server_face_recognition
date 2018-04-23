package org.sanstorik.http_server.server.queries;

import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.ConcreteSqlConnection;
import org.sanstorik.neural_network.face_detection.BufferedFace;
import org.sanstorik.neural_network.face_detection.UserFaceDetector;
import org.sanstorik.neural_network.face_identifying.FaceFeatures;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;

class CropFaceQuery extends ProceedImageQuery {

    CropFaceQuery(boolean doCheckAuth) {
        super(doCheckAuth);
    }


    CropFaceQuery() { super(); }


    @Override protected BufferedImage workOnImage(HttpServletRequest request,
                                                  ConcreteSqlConnection databaseConnection, Token token, File image) {
        UserFaceDetector detector = UserFaceDetector.create();
        BufferedFace response = detector.cropFaceFromImage(image);

        //couldn't find eyes on image and align it
        if (response == null) {
            return null;
        }

        String faceType;

        switch (response.faceType) {
            case FaceFeatures.LEFT_FACE: faceType = "left"; break;
            case FaceFeatures.CENTER_FACE: faceType = "center"; break;
            case FaceFeatures.RIGHT_FACE: faceType = "right"; break;
            default: faceType = "none"; break;
        }

        addParam("face_type", faceType);

        return response.face;
    }
}
