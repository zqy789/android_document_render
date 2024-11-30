

package com.document.render.office.thirdpart.emf.data;

import android.graphics.Point;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class PolyBezierTo16 extends PolyBezierTo {

    public PolyBezierTo16() {
        super(88, 1, null, 0, null);
    }

    public PolyBezierTo16(Rectangle bounds, int numberOfPoints, Point[] points) {
        super(88, 1, bounds, numberOfPoints, points);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        Rectangle r = emf.readRECTL();
        int n = emf.readDWORD();
        return new PolyBezierTo16(r, n, emf.readPOINTS(n));
    }
}
