package org.sanstorik.neural_network;

import com.google.gson.Gson;
import org.sanstorik.neural_network.face_identifying.FaceFeatures;
import org.sanstorik.neural_network.utils.FileUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
       /* FaceRecognizer recognizer = FaceRecognizer.create();
        //FaceFeatures features = recognizer.calculateFeaturesForFace(new File("src/main/resources/.jpg"), "me");
        FaceFeatures secondFeatures = readJson("2.json");
        FaceFeatures thirdFeature = readJson("hryak.json");
        FaceFeatures fouthFeature = readJson("justin1.json");

        //createJson(features, "justin1.json");
        //recognizer.matchTwoFeatureArrays(features, secondFeatures);
        //recognizer.matchTwoFeatureArrays(features, thirdFeature);

        long time = System.currentTimeMillis();
        FaceRecognizer.Prediction prediction = recognizer.identifyUserFromFeaturePool(new File("src/main/resources/3.jpg"),
                new FaceFeatures[]{secondFeatures, thirdFeature, fouthFeature});

        System.out.println(prediction.getPercentage() + " " +
                prediction.getActualFeatures().getFaceLabel() + " identificated = " + prediction.isIdentificated());

        System.out.println("TIME = " + (System.currentTimeMillis() - time)); */

        /*BufferedImage image = null;
        try {
            image = ImageIO.read(FileUtils.loadFile("2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*FaceRecognizer recognizer = FaceRecognizer.create();
        FaceFeatures features = recognizer.calculateFeaturesForFace(new File("src/main/resources/justin-bieber.jpg"), "justin bieber");
        createJson(features, "justin2.json"); */


        /*UserFaceDetector faceDetector = UserFaceDetector.create();
        FaceFeatures secondFeatures = readJson("2.json");
        secondFeatures.setFaceLabel("vitalii");

        FaceFeatures thirdFeature = readJson("hryak.json");
        thirdFeature.setFaceLabel("pig");

        FaceFeatures fouthFeature = readJson("justin1.json");
        fouthFeature.setFaceLabel("justin bieber");

        FaceFeatures fifthFeature = readJson("justin2.json");
        fifthFeature.setFaceLabel("justin bieber");

        FaceRecognizer recognizer = FaceRecognizer.create();
        BufferedImage result = recognizer.identifyUsersOnPhoto(FileUtils.loadFile("poroh.png"),
                new FaceFeatures[]{secondFeatures,thirdFeature,fouthFeature,fifthFeature});

        FileUtils.saveImageAsTemporaryFile(result, "test/result_with_faces.jpg");

        FaceRecognizer faceRecognizer = FaceRecognizer.create(); */

       //FaceRecognizer.Prediction prediction = faceRecognizer.identify(FileUtils.loadFile("poroh.png"), );

        //System.out.println(prediction.getPercentage());

        //FileUtils.saveImageAsTemporaryFile(faceDetector.highlightFacesOnImage(FileUtils.loadFile("egipet.jpg")), "egipet.jpg");

    }
}
