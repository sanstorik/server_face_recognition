package org.sanstorik.neural_network.face_identifying;

import com.google.common.io.ByteStreams;
import org.sanstorik.neural_network.face_detection.BufferedFace;
import org.sanstorik.neural_network.face_detection.Face;
import org.sanstorik.neural_network.face_detection.UserFaceDetector;
import org.sanstorik.neural_network.utils.FileUtils;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.Tensors;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FaceRecognizer {
    public static final class Prediction {
        private float percentage;
        private boolean identified;
        private String username;
        private int identifier;


        private Prediction(float percentage, boolean identified, String username, int identifier) {
            this.percentage = percentage;
            this.identified = identified;
            this.username = username;
            this.identifier = identifier;
        }


        public double getPercentage() {
            return percentage;
        }


        public boolean isIdentified() {
            return identified;
        }


        public String getUsername() {
            return username;
        }


        public int getIdentifier() {
            return identifier;
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
     *
     * @param user             image of user to identify, better to be not high resolution
     *                         because face will be cropped to 160x160px
     * @param expectedFeatures features of a man with whom we are comparing
     * @return prediction whether it's the same person and if it percentage
     */
    public Prediction identify(File user, FullFaceFeatures expectedFeatures) {
        if (user == null || expectedFeatures == null) {
            throw new IllegalArgumentException("should be non null");
        }

        //we still have to crop and normalize face from user image
        FaceFeatures features = calculateFeaturesForFace(user);
        if (features == null) {
            return null;
        }

        return matchTwoFeatureArrays(features,
                expectedFeatures.getFaceFeatures(features.getFaceType()),
                expectedFeatures.getFaceLabel(),
                expectedFeatures.getIdentifier()
        );
    }


    /**
     * Find all user features based on direction it's facing {left; center; right}
     */
    public final Face.Response<FullFaceFeatures, String> calculateFullFeaturesForUser(String faceLabel, File... images) {
        if (images == null || faceLabel == null) {
            throw new IllegalArgumentException("should be non null");
        }

        FullFaceFeatures features = new FullFaceFeatures(faceLabel);

        //we put them with types of faces {left, center, right}
        List<FaceFeatures> typeFeaturesList = new ArrayList<>();
        for (File image : images) {
            FaceFeatures imageFeatures = calculateFeaturesForFace(image);
            typeFeaturesList.add(imageFeatures);
        }


        //assign faces by its type
        for (int faceType = 0; faceType < 3; faceType++) {
            final int innerType = faceType;
            features.setFaceFeatures(innerType, meanOfFeatures(
                    typeFeaturesList.stream()
                            .filter(featuresFilter -> featuresFilter.getFaceType() == innerType)
                            .collect(Collectors.toList()
                            )));
        }

        String errorMessage = "";
        if (features.getFaceFeatures(FaceFeatures.LEFT_FACE) == null) {
            errorMessage = "No photos provided where user is looking left.";
        } else if (features.getFaceFeatures(FaceFeatures.RIGHT_FACE) == null) {
            errorMessage = "No photos provided where user is looking right.";
        } else if (features.getFaceFeatures(FaceFeatures.CENTER_FACE) == null) {
            errorMessage = "No photos provided where user is looking straight.";
        }


        return new Face.Response<>(errorMessage.isEmpty() ? features : null, errorMessage);
    }



    /**
     * Simple calculation on all features of a face in image
     *
     * @param image     of user to identify, better to be not high resolution
     *                  because face will be cropped to 160x160px
     * @return features calculated for a single face
     */
    public final FaceFeatures calculateFeaturesForFace(File image) {
        if (image == null) {
            throw new IllegalArgumentException("should be non null");
        }

        //check if we have faces on image
        BufferedFace face = faceDetector.cropFaceFromImage(image);
        if (face == null || face.face == null) {
            System.out.println("no faces or eyes found");
            return null;
        }

        return passImageThroughNeuralNetwork(face.face, face.faceType);
    }

    /**
     * Finding user in face features pool.
     *
     * @param user              image of user to check, better to be not high resolution
     *                          because face will be cropped to 160x160px
     * @param collectedFeatures expected features of all registered users
     * @return prediction that contains face features with matched user.
     */
    public Prediction identifyUserFromFeaturePool(File user, FullFaceFeatures[] collectedFeatures) {
        FaceFeatures userToFind = calculateFeaturesForFace(user);

        if (userToFind == null) {
            return null;
        }

        return predictBestMatchFromPool(userToFind, collectedFeatures);
    }


    private Prediction predictBestMatchFromPool(FaceFeatures userToFind, FullFaceFeatures[] collectedFeatures) {
        //find best prediction using euclid distance
        Prediction[] predictions = new Prediction[collectedFeatures.length];

        final int inputFaceType = userToFind.getFaceType();
        for (int i = 0; i < collectedFeatures.length; i++) {
            predictions[i] = matchTwoFeatureArrays(userToFind,
                    collectedFeatures[i].getFaceFeatures(inputFaceType),
                    collectedFeatures[i].getFaceLabel(),
                    collectedFeatures[i].getIdentifier()
            );
        }

        return Arrays.stream(predictions).
                max((first, second) -> (Float.compare(first.percentage, second.percentage))).orElse(null);
    }


    /**
     * Mark all registered users on photo
     *
     * @return image with all faces marked - and if possible identified
     */
    public BufferedImage identifyUsersOnPhoto(File image, FullFaceFeatures[] collectedFeatures) {
        //first we take all faces on image
        Face[] faces = faceDetector.getAllFacesFromImage(image);

        //no faces found on photo
        if (faces.length == 0) {
            return null;
        }

        Prediction[] predictions = new Prediction[faces.length];

        //draw probabilities on image with highlighted faces
        for (int i = 0; i < faces.length; i++) {
            FaceFeatures features = passImageThroughNeuralNetwork(faces[i].getCroppedImage(),
                    faces[i].getFaceType());
            predictions[i] = predictBestMatchFromPool(features, collectedFeatures);
        }

        return faceDetector.drawFaceDetection(image, faces, predictions);
    }


    public static FaceRecognizer create() {
        if (faceRecognizer == null) {
            long ms = System.currentTimeMillis();
            faceRecognizer = new FaceRecognizer();
            FileUtils.timeSpent(ms, "INIT");
        }

        return faceRecognizer;
    }


    /**
     * Running neural network
     *
     * @param image cropped, centralized face
     * @return describing of a face based on 128 float features
     */
    private FaceFeatures passImageThroughNeuralNetwork(BufferedImage image, int faceType) {
        FaceFeatures features;

        try (Session session = new Session(graph)) {
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

            features = new FaceFeatures(featuresHolder[0], faceType);

            response.close();
        }

        return features;
    }


    private FaceFeatures meanOfFeatures(List<FaceFeatures> features) {
        if (features == null || features.size() == 0) {
            return null;
        }

        float[] meanValues = new float[features.get(0).getFeatures().length];
        for (int i = 0; i < meanValues.length; i++) {
            for (int j = 0; j < features.size(); j++) {
                meanValues[i] += features.get(j).getFeatures()[i];
            }

            meanValues[i] /= features.size();
        }

        System.out.println(features.size());

        return new FaceFeatures(meanValues, features.get(0).getFaceType());
    }


    /**
     * Check how many percentage do {first} and {second} params have in common
     *
     * @param username of user to be qualified
     * @return prediction of how much they match
     */
    private Prediction matchTwoFeatureArrays(FaceFeatures first, FaceFeatures second,
                                             String username, int identifier) {
        float distance = euclidDistance(first.getFeatures(), second.getFeatures());
        System.out.println("distance with " + username + " = " + distance);

        final float distanceThreshold = 0.6f;
        final float percentageThreshold = 65.0f;

        float percentage = Math.min(100, 100 * distanceThreshold / distance);
        System.out.println("percentage = " + percentage);

        return new Prediction(percentage, percentage >= percentageThreshold, username, identifier);
    }


    /**
     * Difference between two arrays using euclid method
     *
     * @return distance between sets
     */
    private float euclidDistance(float[] first, float[] second) {
        if (first.length != second.length) {
            throw new IllegalArgumentException("should be same size");
        }

        float sum = 0;
        for (int i = 0; i < first.length; i++) {
            sum += Math.abs(first[i] - second[i]);
        }

        return (float) Math.sqrt(sum);
    }


    private byte[] loadGraphDef() {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("save_session/model_face_recognition.pb")) {
            return ByteStreams.toByteArray(is);
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
                java.awt.Color color = new java.awt.Color(rgb);
                image[0][i][j][0] = color.getRed();
                image[0][i][j][1] = color.getGreen();
                image[0][i][j][2] = color.getBlue();
            }
        }
        return image;
    }
}
