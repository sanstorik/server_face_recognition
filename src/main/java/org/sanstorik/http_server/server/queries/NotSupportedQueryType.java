package org.sanstorik.http_server.server.queries;

import org.sanstorik.http_server.database.ConcreteSqlConnection;

import javax.servlet.http.HttpServletRequest;

public class NotSupportedQueryType extends Query {

    NotSupportedQueryType() {
        super(false);
    }


    @Override protected void parseRequest(HttpServletRequest request, ConcreteSqlConnection databaseConnection) {
        errorResponse("Not supported type of a query for this method. If you are using GET method, use POST.");
    }
}
