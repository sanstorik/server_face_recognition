package org.sanstorik.http_server.server.queries;

import org.sanstorik.http_server.database.ConcreteSqlConnection;

import javax.servlet.http.HttpServletRequest;

public class NotSupportedQuery extends Query {

    NotSupportedQuery() {
        super(false);
    }

    @Override protected void parseRequest(HttpServletRequest request, ConcreteSqlConnection connection) {
        errorResponse("This method is not supported. See documentation.");
    }
}
