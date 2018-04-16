package org.sanstorik.http_server.server.queries;

import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.ConcreteSqlConnection;

import javax.servlet.http.HttpServletRequest;

class IdentifyGroupQuery extends Query {

    @Override protected void parseRequest(HttpServletRequest request, ConcreteSqlConnection databaseConnection, Token token) {

    }
}
