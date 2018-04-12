package org.sanstorik.neural_network.face_detection;

import org.sanstorik.neural_network.face_identifying.FaceRecognizer;
import org.sanstorik.neural_network.utils.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;

public class UserFaceDetector {
    private static final String LOADER_PATH = "save_session/haarcascade_frontalcatface.xml";

    private static final int IMAGE_WIDTH = 165;
    private static final int IMAGE_HEIGHT = 170;

    private static UserFaceDetector instance;
    private CascadeClassifier faceDetector;


    private UserFaceDetector() {
        faceDetector = new CascadeClassifier(FileUtils.getResourcesPath() + LOADER_PATH);
    }


    private Mat cropMatFaceFromImage(File image) {
        Mat matImage = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);
        RectVector faces = detectFaces(matImage);

        System.out.println(image.getName() + " found faces = " + faces.size());
        if (faces.size() == 0) {
            return null;
        }

        Rect rect = faces.get(0);

        return cropAndResizeFace(matImage, rect);
    }


    private Mat cropMatByRect(Mat image, Rect rect) {
        Rect staticSizeRect = new Rect(
                rect.x() + (rect.width() / 2) - IMAGE_WIDTH / 2,
                rect.y() + (rect.height() / 2) - IMAGE_HEIGHT / 2,
                IMAGE_WIDTH, IMAGE_HEIGHT
        );

        return new Mat(image, staticSizeRect);
    }


    private Mat cropAndResizeFace(Mat image, Rect rect) {
        Mat face = new Mat(image, rect);
        Mat resizedFace = new Mat();

        resize(face, resizedFace, new Size(IMAGE_WIDTH, IMAGE_HEIGHT));
        return resizedFace;
    }


    /**
     * Finds all faces and highlight them
     * @return image with highlighted
     */
    public Face.Response<BufferedImage, Face[]> getAllFacesFromImage(File image) {
        Mat matImage = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);
        RectVector foundFaces = detectFaces(matImage);
        Face[] faces = new Face[(int)foundFaces.size()];

        System.out.println("FOUND FACES = " + foundFaces.size());
        for (int i = 0; i < foundFaces.size(); i++) {
            Rect frame = foundFaces.get(i);

            BufferedImage croppedFace = matToImage(cropAndResizeFace(matImage, frame));
            FileUtils.saveImageAsTemporaryFile(croppedFace, "/test/i_"+i + ".jpg");

            faces[i] = new Face(frame.x(), frame.y(),
                    frame.width(), frame.height(), croppedFace);
        }

        return new Face.Response<>(matToImage(matImage), faces);
    }


    public void getEyesCoordinates() { /* TODO */ }


    /**
     * Finds face on image and takes face and greyscale it.
     * If several faces are found then it'll take a random one.
     * @return cropped and greyed image 160x170px
     */
    public BufferedImage cropFaceFromImage(File image) {
        if (image == null) {
            throw new IllegalArgumentException("image is null");
        }

        return matToImage(cropMatFaceFromImage(image));
    }


    /**
     * Finds all faces on image and marks it with a rectangle.
     * @return image with marked faces
     */
    public BufferedImage highlightFacesOnImage(File image) {
        Mat matImage = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_ANYCOLOR);
        RectVector faces = detectFaces(matImage);

        for (int i = 0; i < faces.size(); i++) {
            highlightFrame(matImage, faces.get(i));
        }

        return matToImage(matImage);
    }


    /**
     * Creates rectangle and text with probability for detected image
     * @return input image with colored rectangles and probabilities
     */
    public BufferedImage drawFaceDetection(BufferedImage image, Face face, FaceRecognizer.Prediction prediction) {
        String label;
        Scalar color;
        float offsetScale;

        if (prediction.isIdentificated()) {
            label = prediction.getActualFeatures().getFaceLabel() + ";prob=" +
                    String.format("%.2f%%", prediction.getPercentage());
            color = Scalar.RED;
            offsetScale = 0.75f;

        } else {
            label = "none";
            color = Scalar.BLUE;
            offsetScale = 0.25f;
        }


        Mat coloredPicture = putTextOnImage(image, label,
                        face.getLeftTopX() - (int)(face.getLeftTopX() * offsetScale), face.getLeftTopY() - 15
        );

        highlightFrame(coloredPicture, new Rect(
                face.getLeftTopX(), face.getLeftTopY(),
                face.getWidth(), face.getHeight()
        ), color);


        return matToImage(coloredPicture);
    }


    public BufferedImage drawColoredText(BufferedImage image, String text, int pointX, int pointY) {
        return matToImage(putTextOnImage(image, text, pointX, pointY));
    }


    private Mat putTextOnImage(BufferedImage image, String text, int pointX, int pointY) {
        Mat matImage = bufferedImageToMat(image);
        Mat coloredPicture = matImage;

        //if image is grey
        if (matImage.type() == CV_8UC1) {
            coloredPicture = new Mat(matImage.size(), CV_8UC3);
            cvtColor(matImage, coloredPicture, CV_GRAY2BGR);
        }

        putText(coloredPicture, text,
                new Point(pointX,pointY), FONT_HERSHEY_COMPLEX,1.5, Scalar.BLACK, 1, CV_AA, false);

        return coloredPicture;
    }


    private void highlightFrame(Mat image, Rect rect) {
        rectangle(image, rect, Scalar.RED, 3, 5, 0);
    }


    private void highlightFrame(Mat image, Rect rect, Scalar color) {
        rectangle(image, rect, color, 3, 5, 0);
    }


    private BufferedImage matToImage(Mat image) {
        if (image == null) {
            return null;
        }

        byte[] data = new byte[image.size().width() * image.size().height()];
        imencode(".jpg", image, data);

        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new ByteArrayInputStream(data));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bufferedImage;
    }


    // runs 20-30ms what is very good
    private Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat;

        if (bi.getType() == BufferedImage.TYPE_BYTE_GRAY) {
            mat = new Mat(bi.getHeight(), bi.getWidth(), CV_8UC1);
        } else {
            mat = new Mat(bi.getHeight(), bi.getWidth(), CV_8UC3);
        }

        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        //mat.data().put(data);

        return mat;
    }


    private RectVector detectFaces(Mat image) {
        if (image == null) {
            throw new IllegalArgumentException("no image provided");
        }

        RectVector faces = new RectVector();
        faceDetector.detectMultiScale(image, faces);

        RectVector filteredFaces = new RectVector();

        //filter faces to exclude wrong occurrences
        for (int i = 0; i < faces.size(); i++) {
            if (faces.get(i).width() >= 50
                    && faces.get(i).height() >= 50) {
                Rect faceRect = new Rect(
                        faces.get(i).x(),
                        faces.get(i).y(),
                        faces.get(i).width(),
                        faces.get(i).height()
                );

                filteredFaces.push_back(faceRect);
            }
        }

        return filteredFaces;
    }


    public static UserFaceDetector create() {
        if (instance == null) {
            instance = new UserFaceDetector();
        }

        return instance;
    }
}
