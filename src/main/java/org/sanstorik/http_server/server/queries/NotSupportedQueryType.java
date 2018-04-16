package org.sanstorik.http_server.server.queries;

import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.ConcreteSqlConnection;

import javax.servlet.http.HttpServletRequest;

class NotSupportedQueryType extends Query {

    NotSupportedQueryType() {
        super(Type.UNIQUE,false);
    }


    @Override protected void parseRequest(HttpServletRequest request, ConcreteSqlConnection databaseConnection, Token token) {
        errorResponse("Not supported type of a query for this method. If you are using GET method, use POST.");
    }
}
