package org.sanstorik.http_server.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.UUID;

public final class FileUtils {
    private FileUtils() {}


    public static String getRootImagePath() {
        return "images/";
    }


    public static String getRootJsonPath() {
        return "jsons/";
    }


    public static String getRootCachedImagesPath() { return getRootImagePath() + "response/"; }


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