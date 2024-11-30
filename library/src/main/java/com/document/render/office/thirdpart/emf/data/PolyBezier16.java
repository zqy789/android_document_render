

package com.document.render.office.thirdpart.emf.data;

import android.graphics.Point;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class PolyBezier16 extends PolyBezier {

    public PolyBezier16() {
        super(85, 1, null, 0, null);
    }

    public PolyBezier16(Rectangle bounds, int numberOfPoints, Point[] points) {
        super(85, 1, bounds, numberOfPoints, points);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        Rectangle r = emf.readRECTL();
        int n = emf.readDWORD();
        return new PolyBezier16(r, n, emf.readPOINTS(n));
    }

}
