

package com.document.render.office.thirdpart.emf.data;

import android.graphics.Point;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class PolyPolyline extends AbstractPolyPolyline {

    private int start, end;

    public PolyPolyline() {
        super(7, 1, null, null, null);
    }

    public PolyPolyline(Rectangle bounds, int start, int end, int[] numberOfPoints, Point[][] points) {

        super(7, 1, bounds, numberOfPoints, points);

        this.start = start;
        this.end = Math.min(end, numberOfPoints.length - 1);
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
            points[i] = emf.readPOINTL(pc[i]);
        }
        return new PolyPolyline(bounds, 0, np - 1, pc, points);
    }

}
