package org.sanstorik.neural_network.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class FileUtils {

    private FileUtils() { }


    public static File saveImageAsTemporaryFile(BufferedImage image) {
        return saveImageAsTemporaryFile(image, "images/recognizer_cache.jpg");
    }


    public static File saveImageAsTemporaryFile(BufferedImage image, String name) {
        File outputFile = new File(name);

        try {
            if (outputFile.exists()) {
                outputFile.delete();
            }

            outputFile.createNewFile();
            ImageIO.write(image, "jpg", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputFile.exists() ? outputFile : null;
    }


    public static void timeSpent(long start, String label) {
        System.out.println("TIME SPENT ON [" + label + "] = " + (System.currentTimeMillis() - start));
    }
}