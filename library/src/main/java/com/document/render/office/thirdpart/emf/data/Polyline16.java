

package com.document.render.office.thirdpart.emf.data;

import android.graphics.Point;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class Polyline16 extends Polyline {

    public Polyline16() {
        super(87, 1, null, 0, null);
    }

    public Polyline16(Rectangle bounds, int numberOfPoints, Point[] points) {
        super(87, 1, bounds, numberOfPoints, points);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        Rectangle r = emf.readRECTL();
        int n = emf.readDWORD();
        return new Polyline16(r, n, emf.readPOINTS(n));
    }

}
