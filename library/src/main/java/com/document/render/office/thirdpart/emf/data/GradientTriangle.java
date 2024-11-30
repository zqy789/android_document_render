

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFInputStream;

import java.io.IOException;


public class GradientTriangle extends Gradient {

    private int vertex1, vertex2, vertex3;

    public GradientTriangle(int vertex1, int vertex2, int vertex3) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
        this.vertex3 = vertex3;
    }

    public GradientTriangle(EMFInputStream emf) throws IOException {
        vertex1 = emf.readULONG();
        vertex2 = emf.readULONG();
        vertex3 = emf.readULONG();
    }

    public String toString() {
        return "  GradientTriangle: " + vertex1 + ", " + vertex2 + ", " + vertex3;
    }
}
