package org.sanstorik.neural_network.face_identifying;


public class FaceFeatures {

    //128 features to characterize each face
    private float[] features = new float[128];
    private String faceLabel;
    private long identifier;

    {
        faceLabel = String.valueOf("");
    }

    public FaceFeatures() {
    }


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
}
