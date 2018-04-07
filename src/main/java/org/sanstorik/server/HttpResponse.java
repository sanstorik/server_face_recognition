package org.sanstorik.server;


import org.json.JSONObject;

import java.util.Map;

public final class HttpResponse {
    private final static String SUCCESS_STATUS = "success";
    private final static String ERROR_STATUS = "error";

    private Map<String, String> params;
    private String message;
    private String status;

    private HttpResponse(Map<String, String> params) {
        this.params = params;
    }


    private HttpResponse(String message) {
        this.message = message;
    }


    public static HttpResponse create(Map<String, String> params) {
        HttpResponse response = new HttpResponse(params);
        response.status = SUCCESS_STATUS;

        return response;
    }


    public static HttpResponse multipart() {
        return new HttpResponse("");
    }


    public static HttpResponse error(String message) {
        HttpResponse response = new HttpResponse(message);
        response.status = ERROR_STATUS;

        return response;
    }


    public String asJson() {
        JSONObject json = new JSONObject();
        json.put("status", status);

        if (status == SUCCESS_STATUS) {
            JSONObject data = new JSONObject();
            json.put("data", data);

            for (Map.Entry entry : params.entrySet()) {
                data.put(entry.getKey().toString(), entry.getValue().toString());
            }
        } else if (status == ERROR_STATUS) {
            json.put("message", message);
        }

        return json.toString();
    }
}
