package org.sanstorik.neural_network.face_identifying;

public class FullFaceFeatures {
    private FaceFeatures[] faceFeatures = new FaceFeatures[3];
    private String faceLabel = String.valueOf("");

    public FullFaceFeatures() { }


    public String getFaceLabel() {
        return faceLabel;
    }


    public void setFaceLabel(String faceLabel) {
        this.faceLabel = faceLabel;
    }


    public void setFaceFeature(int index, FaceFeatures features) {
        faceFeatures[index] = features;
    }


    public boolean allFacesAreSet() {
        return faceFeatures[0] != null && faceFeatures[1] != null
                && faceFeatures[2] != null;
    }
}
