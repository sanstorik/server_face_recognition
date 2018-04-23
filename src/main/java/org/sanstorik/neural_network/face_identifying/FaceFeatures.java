package org.sanstorik.neural_network.face_identifying;


public class FaceFeatures {
    public static final int LEFT_FACE = 0;
    public static final int CENTER_FACE = 1;
    public static final int RIGHT_FACE = 2;

    //128 features to characterize each face
    private float[] features = new float[128];
    private int faceType = -1;


    private FaceFeatures() { }


    public FaceFeatures(float[] features, int faceType) {
        this.features = features;
        this.faceType = faceType;
    }


    public void setFeatures(float[] features) {
        this.features = features;
    }


    public float[] getFeatures() {
        return features;
    }


    public void setFaceType(int faceType) {
        this.faceType = faceType;
    }


    public int getFaceType() {
        if (faceType <= -1) {
            throw new IllegalStateException("face type is not expected");
        }

        return faceType;
    }
}
