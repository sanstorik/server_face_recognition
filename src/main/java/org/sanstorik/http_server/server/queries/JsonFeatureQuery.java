package org.sanstorik.http_server.server.queries;

import com.google.gson.Gson;
import org.sanstorik.http_server.utils.FileUtils;
import org.sanstorik.neural_network.face_detection.Face;
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


    /**
     * @return error message if query wasn't successful, null otherwise
     */
    protected String createJsonWithFaceFeatures(String jsonPath, String directory, String username,
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
        Face.Response<FullFaceFeatures, String> response =
                faceRecognizer.calculateFullFeaturesForUser(username, images);

        //no faces found on a picture
        if (response.left == null) {
            return response.right;
        }

        boolean isWritten = false;
        try (Writer writer = new FileWriter(json)) {
            Gson gson = new Gson();
            gson.toJson(response.left, writer);
            isWritten = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isWritten ? null : "Wasn't able to create json file.";
    }
}
