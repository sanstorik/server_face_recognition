package org.sanstorik.http_server.server.queries;

import com.google.gson.Gson;
import org.sanstorik.http_server.database.ConcreteSqlConnection;
import org.sanstorik.http_server.database.User;
import org.sanstorik.neural_network.face_identifying.FaceFeatures;

import java.io.File;
import java.util.List;

abstract class FaceFeatureQuery extends Query {

    FaceFeatureQuery(boolean doCheckAuth) {
        super(doCheckAuth);
    }


    protected final FaceFeatures[] getFeaturesOfAllUsers(ConcreteSqlConnection sqlConnection) {
        List<User> users = sqlConnection.getAllUsers();
        FaceFeatures[] faceFeatures = new FaceFeatures[users.size()];

        for (int i = 0; i < faceFeatures.length; i++) {
            FaceFeatures tempFeatures = readFaceFeaturesFromJson(users.get(i).getJsonUrl());

            if (tempFeatures != null) {
                //mark each feature with user id so we know it's him in prediction
                tempFeatures.setIdentifier(users.get(i).getUserId());
                faceFeatures[i] = tempFeatures;
            }
        }

        return faceFeatures;
    }


    private FaceFeatures readFaceFeaturesFromJson(String url) {
        File json = new File(url);

        if (!json.exists() || !json.isFile()) {
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(url, FaceFeatures.class);
    }

}
