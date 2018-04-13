package org.sanstorik.neural_network.face_identifying;

import org.sanstorik.neural_network.face_detection.Face;
import org.sanstorik.neural_network.face_detection.UserFaceDetector;
import org.sanstorik.neural_network.utils.FileUtils;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.Tensors;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class FaceRecognizer {
    public static final class Prediction {
        private float percentage;
        private boolean identificate;
        private FaceFeatures actualFeatures;

        private Prediction(float percentage, boolean identificate, FaceFeatures actualFeatures) {
            this.percentage = percentage;
            this.identificate = identificate;
            this.actualFeatures = actualFeatures;
        }

        public double getPercentage() {
            return percentage;
        }

        public boolean isIdentificated() {
            return identificate;
        }

        public FaceFeatures getActualFeatures() {
            return actualFeatures;
        }
    }

    private Graph graph;
    private UserFaceDetector faceDetector;
    private static FaceRecognizer faceRecognizer;

    private FaceRecognizer() {
        graph = new Graph();
        graph.importGraphDef(loadGraphDef());
        faceDetector = UserFaceDetector.create();
    }

    /**
     * Check whether the input user and previously added features are the same person.
     * Before it proceeds image it crops face and normalizes it.
     * @param user image of user to identify, better to be not high resolution
     *             because face will be cropped to 160x160px
     * @param expectedFeatures features of a man with whom we are comparing
     * @return prediction whether it's the same person and if it percentage
     */
    public Prediction identify(File user, FaceFeatures expectedFeatures) {
        //we still have to crop and normalize face from user image
        FaceFeatures features = calculateFeaturesForFace(user, expectedFeatures.getFaceLabel());
        if (features == null) {
            return null;
        }

        return matchTwoFeatureArrays(features, expectedFeatures);
    }


    /**
     * Simple calculation on all features of a face in image
     * @param image of user to identify, better to be not high resolution
     *             because face will be cropped to 160x160px
     * @param faceLabel label of face to be made
     * @return features calculated for a single face
     */
    public final FaceFeatures calculateFeaturesForFace(File image, String faceLabel) {
        if (image == null || faceLabel == null) {
            throw new IllegalArgumentException("should be non null");
        }

        //check if we have faces on image
        BufferedImage croppedImage = faceDetector.cropFaceFromImage(image);
        if (croppedImage == null) {
            System.out.println("no faces found");
            return null;
        }

        FaceFeatures features = passImageThroughNeuralNetwork(croppedImage);
        features.setFaceLabel(faceLabel);

        return features;
    }

    /**
     * Finding user in face features pool.
     * @param user image of user to check, better to be not high resolution
     *             because face will be cropped to 160x160px
     * @param collectedFeatures expected features of all registered users
     * @return prediction that contains face features with matched user.
     */
    public Prediction identifyUserFromFeaturePool(File user, FaceFeatures[] collectedFeatures) {
        FaceFeatures userToFind = calculateFeaturesForFace(user, "user");

        if (user == null || userToFind == null) {
            return null;
        }

        return predictBestMatchFromPool(userToFind, collectedFeatures);
    }


    //predict with allready cropped face
    private Prediction identifyUserFromFeaturePoolWithCropped(BufferedImage image, FaceFeatures[] collectedFeatures) {
        FaceFeatures userToFind = passImageThroughNeuralNetwork(image);

        return predictBestMatchFromPool(userToFind, collectedFeatures);
    }


    private Prediction predictBestMatchFromPool(FaceFeatures userToFind, FaceFeatures[] collectedFeatures) {
        //find best prediction using euclid distance
        Prediction[] predictions = new Prediction[collectedFeatures.length];
        for (int i = 0; i < collectedFeatures.length; i++) {
            predictions[i] = matchTwoFeatureArrays(userToFind, collectedFeatures[i]);
            predictions[i].actualFeatures = collectedFeatures[i];
        }

        return Arrays.stream(predictions).
                max( (first, second) -> (Float.compare(first.percentage,second.percentage))).orElse(null);
    }


    /**
     * Mark all registered users on photo
     * @return image with all faces marked - and if possible identified
     */
    public BufferedImage identifyUsersOnPhoto(File image, FaceFeatures[] collectedFeatures) {
        //first we take all faces on image
        Face.Response<BufferedImage, Face[]> facesResponse = faceDetector.getAllFacesFromImage(image);
        Face[] faces = facesResponse.right;
        BufferedImage imageWithHighlithedFaces = facesResponse.left;

        //no faces found on photo
        if (faces.length == 0) {
            return null;
        }

        //draw probabilities on image with highlighed faces
        for (Face face : faces) {
            Prediction prediction = identifyUserFromFeaturePoolWithCropped(
                    face.getCroppedImage(),
                    collectedFeatures);

            //mark(text, rectangle) users faces
            if (prediction != null) {
                imageWithHighlithedFaces = faceDetector.drawFaceDetection(imageWithHighlithedFaces, face, prediction);
            }
        }

        return imageWithHighlithedFaces;
    }


    public static FaceRecognizer create() {
        if (faceRecognizer == null) {
            long ms = System.currentTimeMillis();
            faceRecognizer = new FaceRecognizer();
            System.out.println("Init time = " + String.valueOf(System.currentTimeMillis() - ms) + "ms");
        }

        return faceRecognizer;
    }


    /**
     * Running neural network
     * @param image cropped, centralized face
     * @return describing of a face based on 128 float features
     */
    private FaceFeatures passImageThroughNeuralNetwork(BufferedImage image) {
        FaceFeatures features;

        long timeStart = System.currentTimeMillis();
        try (Session session = new Session(graph)) {
            FileUtils.timeSpent(timeStart, "SESSION START");
            Tensor<Float> feedImage = Tensors.create(imageToMultiDimensionalArray(image));

            long timeResponse = System.currentTimeMillis();

            Tensor<Float> response = session.runner()
                    .feed("input", feedImage)
                    .feed("phase_train", Tensor.create(false))
                    .fetch("embeddings")
                    .run().get(0)
                    .expect(Float.class);

            FileUtils.timeSpent(timeResponse, "RESPONSE");

            final long[] shape = response.shape();

            //first dimension should return 1 as for image with normal size
            //second dimension should give 128 characteristics of face
            if (shape[0] != 1 || shape[1] != 128) {
                throw new IllegalStateException("illegal output values: 1 = " + shape[0] + " 2 = " + shape[1]);
            }

            float[][] featuresHolder = new float[1][128];
            response.copyTo(featuresHolder);

            features = new FaceFeatures();
            features.setFeatures(featuresHolder[0]);

            response.close();
        }

        return features;
    }


    /**
     * Check how many percentage do {first} and {second} params have in common
     * @return prediction of how much they match
     */
    private Prediction matchTwoFeatureArrays(FaceFeatures first, FaceFeatures second) {
        float distance = euclidDistance(first.getFeatures(), second.getFeatures());
        System.out.println("distance = " + distance);

        final float distanceThreshold = 0.6f;
        final float percentageThreshold = 60;

        float percentage = Math.min(100, 100 * distanceThreshold / distance);
        System.out.println("percentage = " + percentage);

        return new Prediction(percentage, percentage >= percentageThreshold, first);
    }


    /**
     * Difference between two arrays using euclid method
     * @return distance between sets
     */
    private float euclidDistance(float[] first, float[] second) {
        if (first.length != second.length) {
            throw  new IllegalArgumentException("should be same size");
        }

        float sum = 0;
        for (int i = 0; i < first.length; i++) {
            sum += Math.abs(first[i] - second[i]);
        }

        return (float)Math.sqrt(sum);
    }


    private byte[] loadGraphDef() {
        System.out.println("LOADING GRAPH");
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("save_session/model_face_recognition.pb")) {
            return sun.misc.IOUtils.readFully(is, -1, true);
        } catch (IOException e) {
           throw new RuntimeException("couldn't load graph");
        }
    }


    private static float[][][][] imageToMultiDimensionalArray(BufferedImage bi) {
        if (bi == null) {
            throw new IllegalArgumentException("image for neural network is null");
        }

        int height = bi.getHeight(), width = bi.getWidth(), depth = 3;
        float image[][][][] = new float[1][width][height][depth];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                int rgb = bi.getRGB(i, j);
                Color color = new Color(rgb);
                image[0][i][j][0] = color.getRed();
                image[0][i][j][1] = color.getGreen();
                image[0][i][j][2] = color.getBlue();
            }
        }
        return image;
    }
}
