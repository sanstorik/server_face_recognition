package org.sanstorik.http_server;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HttpResponse {
    private final static String SUCCESS_STATUS = "success";
    private final static String ERROR_STATUS = "error";

    private Map<String, String> params;
    private Map<String, Map<String, String>> embeddedEntries;
    private List<double[]> embeddedArrays;
    private List<String> embeddedArrayKeys;
    private String errorMessage = "Unexpected error.";
    private String status;
    {
        this.embeddedArrayKeys = new ArrayList<>();
        this.embeddedArrays = new ArrayList<>();
        this.params = new HashMap<>();
        this.embeddedEntries = new HashMap<>();
        this.status = SUCCESS_STATUS;
    }

    private HttpResponse() { }


    private HttpResponse(Map<String, String> params) {
        this.params = params;
    }


    private HttpResponse(String message) {
        this.errorMessage = message;
    }


    public void setStatusSuccess() {
        this.status = SUCCESS_STATUS;
    }


    public void setStatusError() {
        this.status = ERROR_STATUS;
    }


    public void addParam(String key, String value) {
        params.put(key, value);
    }


    public void addEmbeddedEntry(String key, Map<String, String> params) {
        this.embeddedEntries.put(key, params);
    }


    public void addEmbeddedArray(String key, double[] values) {
        this.embeddedArrayKeys.add(key);
        this.embeddedArrays.add(values);
    }


    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    public static HttpResponse fromTemplate() {
        return new HttpResponse();
    }


    public static HttpResponse create(Map<String, String> params) {
        HttpResponse response = new HttpResponse(params);
        response.status = SUCCESS_STATUS;

        return response;
    }


    public static HttpResponse error(String message) {
        HttpResponse response = new HttpResponse(message);
        response.status = ERROR_STATUS;

        return response;
    }


    public String asJson() {
        JSONObject json = new JSONObject();
        json.put("status", status);

        if (status.equals(ERROR_STATUS)) {
            json.put("message", errorMessage);
        }

        //normal output
        else if (status.equals(SUCCESS_STATUS) && (!params.isEmpty() || !embeddedArrayKeys.isEmpty())) {
            JSONObject data = new JSONObject();
            json.put("data", data);

            //main data
            for (Map.Entry entry : params.entrySet()) {
                data.put(entry.getKey().toString(), entry.getValue().toString());
            }

            //additional entries
            for (Map.Entry entry: embeddedEntries.entrySet()) {
                Map<String, String> valuesMap = (Map<String,String>)entry.getValue();
                String valuesMapKey = entry.getKey().toString();
                JSONObject newEntry = new JSONObject();

                for (Map.Entry values: valuesMap.entrySet()) {
                    newEntry.put(values.getKey().toString(), values.getValue().toString());
                }

                data.put(valuesMapKey, newEntry);
            }

            //additional arrays
            if (!embeddedArrays.isEmpty()) {
                for (int i = 0; i < embeddedArrays.size(); i++) {
                    JSONArray array = new JSONArray();

                    for (int j = 0; j < embeddedArrays.get(i).length; j++) {
                        array.put(embeddedArrays.get(i)[j]);
                    }

                    data.put(embeddedArrayKeys.get(i), array);
                }
            }
        } else if(status.equals(SUCCESS_STATUS)) {
            json.put("message", "Query succeded");
        }

        return json.toString();
    }
}
