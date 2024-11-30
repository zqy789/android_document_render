

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFInputStream;

import java.io.IOException;


public class GradientRectangle extends Gradient {

    private int upperLeft, lowerRight;

    public GradientRectangle(int upperLeft, int lowerRight) {
        this.upperLeft = upperLeft;
        this.lowerRight = lowerRight;
    }

    public GradientRectangle(EMFInputStream emf) throws IOException {
        upperLeft = emf.readULONG();
        lowerRight = emf.readULONG();
    }

    public String toString() {
        return "  GradientRectangle: " + upperLeft + ", " + lowerRight;
    }
}
