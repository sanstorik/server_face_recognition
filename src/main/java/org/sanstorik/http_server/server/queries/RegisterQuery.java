package org.sanstorik.http_server.server.queries;

import org.eclipse.jetty.util.MultiPartInputStreamParser;
import org.eclipse.jetty.util.MultiPartOutputStream;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RegisterQuery extends Query {

    @Override protected void parseRequest(HttpServletRequest request) {
    }
}
