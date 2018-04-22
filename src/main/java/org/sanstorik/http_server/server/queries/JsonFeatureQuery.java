package org.sanstorik.http_server.server.queries;

import com.google.gson.Gson;
import org.sanstorik.http_server.utils.FileUtils;
import org.sanstorik.neural_network.face_identifying.FaceFeatures;
import org.sanstorik.neural_network.face_identifying.FaceRecognizer;
import org.sanstorik.neural_network.face_identifying.FullFaceFeatures;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

abstract class JsonFeatureQuery extends Query {

    JsonFeatureQuery(boolean doCheckAuth) { super(doCheckAuth); }


    JsonFeatureQuery() { }


    protected boolean createJsonWithFaceFeatures(String jsonPath, String directory, String username,
                                                 File... images) {
        File json = new File(jsonPath);

        //create full path dirs
        try {
            new File(FileUtils.getRootJsonPath() + directory).mkdirs();

            if (!json.exists()) {
                json.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        FaceRecognizer faceRecognizer = FaceRecognizer.create();
        FullFaceFeatures features = faceRecognizer.calculateFullFeaturesForUser(username, images);

        //no faces found on a picture
        if (features == null) {
            return false;
        }

        boolean isWritten = false;
        try (Writer writer = new FileWriter(json)) {
            Gson gson = new Gson();
            gson.toJson(features, writer);
            isWritten = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isWritten;
    }
}
