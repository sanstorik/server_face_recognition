package org.sanstorik.neural_network.face_identifying;

public class FullFaceFeatures {
    // {left - center - right }
    private FaceFeatures[] faceFeatures = new FaceFeatures[3];
    private String faceLabel = String.valueOf("");

    public FullFaceFeatures() { }


    public String getFaceLabel() {
        return faceLabel;
    }


    public void setFaceLabel(String faceLabel) {
        this.faceLabel = faceLabel;
    }


    public void setFaceFeatures(int index, FaceFeatures features) {
        faceFeatures[index] = features;
    }


    public FaceFeatures getFaceFeatures(int faceType) {
        return faceFeatures[faceType];
    }


    public boolean allFacesAreSet() {
        return faceFeatures[0] != null && faceFeatures[1] != null
                && faceFeatures[2] != null;
    }
}
