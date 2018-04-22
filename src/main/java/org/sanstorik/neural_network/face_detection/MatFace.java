package org.sanstorik.neural_network.face_detection;

import org.bytedeco.javacpp.opencv_core;

class MatFace {
    public opencv_core.Mat face;
    public int faceType;

    MatFace(opencv_core.Mat face, int faceType) {
        this.face = face;
        this.faceType = faceType;
    }
}
