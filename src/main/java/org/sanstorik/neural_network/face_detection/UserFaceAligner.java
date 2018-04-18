package org.sanstorik.neural_network.face_detection;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.bytedeco.javacpp.opencv_core;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.flandmark.*;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;

public class UserFaceAligner {
    private enum landmark_pos {
        FACE_CENTER(0),
        LEFT_EYE_INNER(1),
        RIGHT_EYE_INNER(2),
        MOUTH_LEFT(3),
        MOUTH_RIGHT(4),
        LEFT_EYE_OUTER(5),
        RIGHT_EYE_OUTER(6),
        NOSE_CENTER(7),
        LEFT_EYE_ALIGN(8),
        RIGHT_EYE_ALIGN(9);

        public int pos;

        private landmark_pos(int pos) {
            this.pos = pos;
        }
    };

    private static final String LOADER_URL = "save_session/flandmark_model.dat";

    private static UserFaceAligner userFaceAligner;
    private final FLANDMARK_Model flandmarkModel;

    private UserFaceAligner() {
        flandmarkModel = flandmark_init(
                getClass().getClassLoader().getResource(LOADER_URL).getPath()
        );
    }


    public Mat align(Mat face, Rect bounds) {
        int[] sizes = new int[]{ bounds.x(), bounds.y(),
                bounds.x() + bounds.width(), bounds.y() + bounds.height() };

        final double[] landmarksCoord = new double[2 * flandmarkModel.data().options().M()];

        flandmark_detect(new IplImage(face), sizes, flandmarkModel, landmarksCoord);

        for (int i = 0; i < landmarksCoord.length; i++) {
            System.out.println("land_"+ i + " " + landmarksCoord[i]);
        }


        List<Point2d> landmarks = new ArrayList<>(5);

        for (int i = 0; i < this.flandmarkModel.data().options().M(); i++) {
            landmarks.add(new Point2d(landmarksCoord[2 * i], landmarksCoord[2 * i + 1]));
        }

        SimpleRegression linearRegression = new SimpleRegression();

        linearRegression.addData(
                landmarks.get(landmark_pos.LEFT_EYE_OUTER.pos).x(),
                landmarks.get(landmark_pos.LEFT_EYE_OUTER.pos).y()
        );

        linearRegression.addData(
                landmarks.get(landmark_pos.LEFT_EYE_INNER.pos).x(),
                landmarks.get(landmark_pos.LEFT_EYE_INNER.pos).y()
        );

        linearRegression.addData(
                landmarks.get(landmark_pos.RIGHT_EYE_INNER.pos).x(),
                landmarks.get(landmark_pos.RIGHT_EYE_INNER.pos).y()
        );

        linearRegression.addData(
                landmarks.get(landmark_pos.RIGHT_EYE_OUTER.pos).x(),
                landmarks.get(landmark_pos.RIGHT_EYE_OUTER.pos).y()
        );

        Point2d alignedLeftEyePos = new Point2d(
                landmarks.get(landmark_pos.LEFT_EYE_OUTER.pos).x(),
                linearRegression.predict(landmarks.get(landmark_pos.LEFT_EYE_OUTER.pos).x())
        );

        Point2d alignedRightEyePos = new Point2d(
                landmarks.get(landmark_pos.RIGHT_EYE_OUTER.pos).x(),
                linearRegression.predict(landmarks.get(landmark_pos.RIGHT_EYE_OUTER.pos).x())
        );

        highlightFrame(face, landmarks.get(landmark_pos.FACE_CENTER.pos));

        highlightFrame(face, alignedLeftEyePos);
        highlightFrame(face, alignedRightEyePos);

        return face;
    }

    private void highlightFrame(Mat image, Point2d point2d) {
        Rect rect = new Rect(
                (int)point2d.x(), (int)point2d.y(), 20, 20
        );

        rectangle(image, rect, Scalar.RED, 3, 5, 0);
    }


    public static UserFaceAligner create() {
        if (userFaceAligner == null) {
            userFaceAligner = new UserFaceAligner();
        }

        return userFaceAligner;
    }
}
