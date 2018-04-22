package org.sanstorik.http_server.server.queries;

import com.google.gson.Gson;
import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.ConcreteSqlConnection;
import org.sanstorik.http_server.database.User;
import org.sanstorik.neural_network.face_identifying.FaceFeatures;
import org.sanstorik.neural_network.face_identifying.FullFaceFeatures;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

abstract class FaceFeatureQuery extends Query {

    FaceFeatureQuery(boolean doCheckAuth) {
        super(doCheckAuth);
    }


    FaceFeatureQuery() { }


    protected final FullFaceFeatures[] getFeaturesOfAllUsers(ConcreteSqlConnection sqlConnection) {
        List<User> users = sqlConnection.getAllUsers();
        if (users == null || users.isEmpty()) {
            return null;
        }

        FullFaceFeatures[] faceFeatures = new FullFaceFeatures[users.size()];

        for (int i = 0; i < faceFeatures.length; i++) {
            FullFaceFeatures tempFeatures = readFaceFeaturesFromJson(users.get(i).getJsonUrl());

            if (tempFeatures != null) {
                //mark each feature with user id so we know it's him in prediction
                tempFeatures.setIdentifier(users.get(i).getUserId());
                faceFeatures[i] = tempFeatures;
            }
        }

        return faceFeatures;
    }


    final FullFaceFeatures getFeatureOfUser(int userId, ConcreteSqlConnection sqlConnection) {
        User user = sqlConnection.getUserById(userId);
        if (user == null) {
            return null;
        }

        FullFaceFeatures features = readFaceFeaturesFromJson(user.getJsonUrl());

        if (features != null) {
            features.setIdentifier(userId);
        }

        return features;
    }


    private FullFaceFeatures readFaceFeaturesFromJson(String url) {
        File json = new File(url);

        if (!json.exists() || !json.isFile()) {
            return null;
        }

        FullFaceFeatures features = null;

        try {
            features = new Gson().fromJson(new FileReader(json), FullFaceFeatures.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return features;
    }

}
