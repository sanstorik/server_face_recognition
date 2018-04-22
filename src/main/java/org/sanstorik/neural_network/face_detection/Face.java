package org.sanstorik.neural_network.face_detection;

import java.awt.image.BufferedImage;

public class Face {
    public static class Response<T1, T2> {
        public T1 left;
        public T2 right;

        public Response(T1 left, T2 right) {
            this.left = left;
            this.right = right;
        }
    }

    private BufferedImage croppedImage;
    private int faceType = -1;

    //position of face on the original image
    private int leftTopX;
    private int leftTopY;
    private int width;
    private int height;


    public Face(int leftTopX, int leftTopY, int width, int height,
                int faceType,
                BufferedImage croppedImage) {
        this.leftTopX = leftTopX;
        this.leftTopY = leftTopY;
        this.width = width;
        this.height = height;
        this.croppedImage = croppedImage;
        this.faceType = faceType;
    }


    public BufferedImage getCroppedImage() {
        return croppedImage;
    }


    public int getLeftTopX() {
        return leftTopX;
    }


    public int getLeftTopY() {
        return leftTopY;
    }


    public int getWidth() {
        return width;
    }


    public int getHeight() {
        return height;
    }


    public int getFaceType() {
        return faceType;
    }
}
