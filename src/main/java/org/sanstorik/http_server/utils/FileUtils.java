package org.sanstorik.http_server.utils;

import java.util.UUID;

public final class FileUtils {
    private FileUtils() {}


    public static String getRootImagePath() {
        return "images/";
    }


    public static String getRootJsonPath() {
        return "jsons/";
    }


    public static String getRootCachedImagesDirectoryName() { return "response/"; }


    public static String addHostUrl(String filePath) {
        return System.getProperty("IMAGE_URL_ROOT") + "/" + filePath;
    }


    public static String generateRandomImageName() {
        return generateRandomString() + ".jpg";
    }


    public static String generateRandomString() {
        return UUID.randomUUID().toString().replace("-","");
    }
}