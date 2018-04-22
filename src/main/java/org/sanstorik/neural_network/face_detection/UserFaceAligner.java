package org.sanstorik.neural_network.face_detection;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.bytedeco.javacpp.indexer.DoubleRawIndexer;
import org.sanstorik.neural_network.face_identifying.FaceFeatures;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.flandmark.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.getRotationMatrix2D;
import static org.bytedeco.javacpp.opencv_imgproc.warpAffine;

class UserFaceAligner {
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

        landmark_pos(int pos) {
            this.pos = pos;
        }
    }

    private static final String LOADER_URL = "save_session/flandmark_model.dat";

    private static UserFaceAligner userFaceAligner;
    private final FLANDMARK_Model flandmarkModel;


    private UserFaceAligner() {
        URL loaderUrl = getClass().getClassLoader().getResource(LOADER_URL);
        if (loaderUrl == null) throw new IllegalStateException("Landmark model not found.");

        flandmarkModel = flandmark_init(loaderUrl.getPath());
    }


    /**
     * Takes photo, finds eyes and crops face
     * @param image greyscaled full-res image
     * @param bounds bounds of a found face
     * @return cropped and aligned face with initial face sizes
     */
    public MatFace align(Mat image, Rect bounds) {
        List<Point2d> landmarks = getEyesLandmarks(image, bounds);

        if (landmarks == null) {
            System.out.println("No landmarks");
            return null;
        }

        addAlignedEyesPos(landmarks);
        Mat rotatedFace = rotateImageAndExtractFace(image, landmarks,
                UserFaceDetector.INITIAL_WIDTH, UserFaceDetector.INITIAL_HEIGHT);
        int faceType = findFaceType(landmarks);

        return new MatFace(rotatedFace, faceType);
    }


    /**
     * @param image greyscaled full-res image
     * @param bounds bounds of a found face
     * @return array with size two(2) of found eyes
     */
    public Point2d[] getEyesCenterCoordinates(Mat image, Rect bounds) {
        List<Point2d> landmarks = getEyesLandmarks(image, bounds);

        if (landmarks == null) {
            return null;
        }

        Point2d leftEyeOuter = landmarks.get(landmark_pos.LEFT_EYE_OUTER.pos);
        Point2d leftEyeInner = landmarks.get(landmark_pos.LEFT_EYE_INNER.pos);

        Point2d rightEyeOuter = landmarks.get(landmark_pos.RIGHT_EYE_OUTER.pos);
        Point2d rightEyeInner = landmarks.get(landmark_pos.RIGHT_EYE_INNER.pos);

        return new Point2d[] { center(leftEyeOuter, leftEyeInner), center(rightEyeOuter, rightEyeInner) };
    }


    private int findFaceType(List<Point2d> landmarks) {
        int type;

        final double center = landmarks.get(landmark_pos.NOSE_CENTER.pos).x();
        final double left_eye = landmarks.get(landmark_pos.LEFT_EYE_OUTER.pos).x();
        final double right_eye = landmarks.get(landmark_pos.RIGHT_EYE_OUTER.pos).x();

        //distances between them
        double left_center = Math.abs(left_eye - center);
        double right_center = Math.abs(center - right_eye);

        right_center = right_center == 0 ? 1e-3 : right_center;
        left_center = left_center == 0 ? 1e-3 : left_center;


        System.out.println("right = " + right_center / left_center);
        System.out.println("left = " + left_center / right_center);
        final double scale_treshold = 2.0;
        if (right_center / left_center >= scale_treshold) {
            type = FaceFeatures.RIGHT_FACE;
        } else if ( left_center / right_center >= scale_treshold) {
            type = FaceFeatures.LEFT_FACE;
        } else {
            type = FaceFeatures.CENTER_FACE;
        }

        System.out.println("Found face type = " + type);

        return type;
    }


    private Point2d center(Point2d left, Point2d right) {
        return new Point2d(
                (left.x() + right.x()) * 0.5,
                (left.y() + right.y()) * 0.5
        );
    }


    private List<Point2d> getEyesLandmarks(Mat image, Rect bounds) {
        int[] sizes = new int[]{ bounds.x(), bounds.y(),
                bounds.x() + bounds.width(), bounds.y() + bounds.height() };

        final double[] landmarksCoord = new double[2 * flandmarkModel.data().options().M()];

        flandmark_detect(new IplImage(image), sizes, flandmarkModel, landmarksCoord);

        boolean found = false;
        for(int i = 0; i < landmarksCoord.length; i++) {
            if (landmarksCoord[i] != 0.0) {
                found = true;
            }
        }

        //couldn't find any landmarks for eyes
        if (!found) {
            return null;
        }

        List<Point2d> landmarks = new ArrayList<>(5);

        for (int i = 0; i < this.flandmarkModel.data().options().M(); i++) {
            landmarks.add(new Point2d(landmarksCoord[2 * i], landmarksCoord[2 * i + 1]));
        }

        return landmarks;
    }


    private void addAlignedEyesPos(List<Point2d> landmarks) {
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

        landmarks.add(alignedLeftEyePos);
        landmarks.add(alignedRightEyePos);
    }


    private Mat rotateImageAndExtractFace(Mat image, List<Point2d> landmarks,  int faceWidth, int faceHeight) {
        final double DESIRED_LEFT_EYE_X = 0.27;
        final double DESIRED_LEFT_EYE_Y = 0.4;

        Point2d leftEye = landmarks.get(landmark_pos.LEFT_EYE_ALIGN.pos);
        Point2d rightEye = landmarks.get(landmark_pos.RIGHT_EYE_ALIGN.pos);

        Point2f eyesCenter = new Point2f(
                (float)((leftEye.x() + rightEye.x()) * 0.5f),
                (float)((leftEye.y() + rightEye.y()) * 0.5f)
        );

        final double dy = (rightEye.y() - leftEye.y());
        final double dx = (rightEye.x() - leftEye.x());
        final double len = Math.sqrt(dx * dx + dy * dy);
        final double angle = Math.atan2(dy, dx) * 180.0 / CV_PI;

        final double DESIRED_RIGHT_EYE_X = 1 - DESIRED_LEFT_EYE_X;
        final double desiredLen = (DESIRED_RIGHT_EYE_X - DESIRED_LEFT_EYE_X) * faceWidth;
        double scale = desiredLen / len;

        Mat rotMat = getRotationMatrix2D(eyesCenter, angle, scale);
        DoubleRawIndexer indexer = rotMat.createIndexer();
        indexer.put(0, 2, indexer.get(0, 2) + (faceWidth * 0.5 - eyesCenter.x()));
        indexer.put(1, 2, indexer.get(1, 2) + (faceHeight * DESIRED_LEFT_EYE_Y - eyesCenter.y()));

        Mat destImage = new Mat(faceHeight, faceWidth, CV_8U, new Scalar(128));
        warpAffine(image, destImage, rotMat, destImage.size());

        return destImage;
    }


    public static UserFaceAligner create() {
        if (userFaceAligner == null) {
            userFaceAligner = new UserFaceAligner();
        }

        return userFaceAligner;
    }
}
