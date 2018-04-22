package org.sanstorik.http_server.server.queries;

import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.ConcreteSqlConnection;
import org.sanstorik.http_server.utils.FileUtils;
import org.sanstorik.neural_network.face_detection.Face;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

class UpdateUserPhotoQuery extends JsonFeatureQuery {


    @Override protected void parseRequest(HttpServletRequest request, ConcreteSqlConnection databaseConnection, Token token) {
        errorResponse("Not supported yet.");
    }
}
