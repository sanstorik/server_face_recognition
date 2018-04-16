package org.sanstorik.http_server.server.queries;

import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.ConcreteSqlConnection;

import javax.servlet.http.HttpServletRequest;

class NotSupportedQuery extends Query {

    NotSupportedQuery() {
        super(Type.UNIQUE, false);
    }

    @Override protected void parseRequest(HttpServletRequest request, ConcreteSqlConnection connection, Token token) {
        errorResponse("This method is not supported. See the documentation.");
    }
}
