

package com.document.render.office.thirdpart.emf.data;

import android.graphics.Point;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class PolyPolygon16 extends AbstractPolyPolygon {

    private int numberOfPolys;

    public PolyPolygon16() {
        super(91, 1, null, null, null);
    }

    public PolyPolygon16(Rectangle bounds, int numberOfPolys, int[] numberOfPoints, Point[][] points) {

        super(91, 1, bounds, numberOfPoints, points);
        this.numberOfPolys = numberOfPolys;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        Rectangle bounds = emf.readRECTL();
        int np = emf.readDWORD();

        emf.readDWORD();
        int[] pc = new int[np];
        Point[][] points = new Point[np][];

        for (int i = 0; i < np; i++) {
            pc[i] = emf.readDWORD();
            points[i] = new Point[pc[i]];
        }

        for (int i = 0; i < np; i++) {
            points[i] = emf.readPOINTS(pc[i]);
        }

        return new PolyPolygon16(bounds, np, pc, points);
    }
}
