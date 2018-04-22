package org.sanstorik.neural_network.face_identifying;


public class FaceFeatures {
    public static final int LEFT_FACE = 0;
    public static final int CENTER_FACE = 1;
    public static final int RIGHT_FACE = 2;

    //128 features to characterize each face
    private float[] features = new float[128];
    private String faceLabel = String.valueOf("");
    private long identifier;
    private int faceType = -1;


    public FaceFeatures() { }


    public FaceFeatures(float[] features) {
        this.features = features;
    }


    public FaceFeatures(float[] features, String faceLabel) {
        this.features = features;
        this.faceLabel = faceLabel;
    }


    public void setFaceLabel(String faceLabel) {
        this.faceLabel = faceLabel;
    }


    public String getFaceLabel() {
        return faceLabel;
    }


    public void setFeatures(float[] features) {
        this.features = features;
    }


    public float[] getFeatures() {
        return features;
    }


    public void setIdentifier(long identifier) {
        this.identifier = identifier;
    }


    public long getIdentifier() {
        return identifier;
    }


    public void setFaceType(int faceType) {
        this.faceType = faceType;
    }


    public int getFaceType() {
        if (faceType <= -1) {
            throw new IllegalStateException("face type is not normal");
        }

        return faceType;
    }
}
