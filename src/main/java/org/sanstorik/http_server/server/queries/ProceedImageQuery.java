package org.sanstorik.http_server.server.queries;

import org.sanstorik.http_server.Token;
import org.sanstorik.http_server.database.ConcreteSqlConnection;
import org.sanstorik.http_server.utils.FileUtils;
import org.sanstorik.neural_network.face_detection.Face;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

abstract class ProceedImageQuery extends FaceFeatureQuery {


    protected final void proceedImage(HttpServletRequest request, ConcreteSqlConnection databaseConnection, Token token) {
        String imageName = FileUtils.generateRandomImageName();
        Face.Response<File, String> response = getInputImage(request, imageName);

        //no input image
        if (response.left == null) {
            errorResponse("There is no image or couldn't read it.");
            return;
        }

        BufferedImage image = workOnImage(request, databaseConnection, token, response.left);

        if (image != null && writeImageToFileSystem(image, response.left)) {
            addParam("image_url", FileUtils.addHostUrl(
                    FileUtils.getRootImagePath() + FileUtils.getRootCachedImagesDirectoryName() + imageName));
        } else {
            errorResponse("Output image is null. Neural network couldn't proceed or write it. " +
                    "Maybe faces haven't been found.");
        }
    }


    protected Face.Response<File, String> getInputImage(HttpServletRequest request, String fileName) {
        return readImageFromMultipartRequest(request, "image",
                FileUtils.getRootCachedImagesDirectoryName(), fileName);
    }


    protected abstract BufferedImage workOnImage(HttpServletRequest request,
                                       ConcreteSqlConnection databaseConnection, Token token, File image);


    protected boolean writeImageToFileSystem(BufferedImage inputImage, File outputFile) {
        boolean fileIsWritten = false;
        try {
            if (outputFile.exists()) {
                fileIsWritten = ImageIO.write(inputImage, "jpg", outputFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileIsWritten;
    }


    @Override protected final void parseRequest(HttpServletRequest request, ConcreteSqlConnection databaseConnection, Token token) {
        proceedImage(request, databaseConnection, token);
    }
}
