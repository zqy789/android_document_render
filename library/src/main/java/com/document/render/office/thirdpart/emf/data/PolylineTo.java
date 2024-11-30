
package com.document.render.office.thirdpart.emf.data;

import android.graphics.Point;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.java.awt.geom.GeneralPath;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class PolylineTo extends AbstractPolygon {

    public PolylineTo() {
        super(6, 1, null, 0, null);
    }

    public PolylineTo(Rectangle bounds, int numberOfPoints, Point[] points) {
        this(6, 1, bounds, numberOfPoints, points);
    }

    protected PolylineTo(int id, int version, Rectangle bounds, int numberOfPoints, Point[] points) {
        super(id, version, bounds, numberOfPoints, points);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        Rectangle r = emf.readRECTL();
        int n = emf.readDWORD();
        return new PolylineTo(r, n, emf.readPOINTL(n));
    }


    public void render(EMFRenderer renderer) {
        Point[] points = getPoints();
        int numberOfPoints = getNumberOfPoints();
        GeneralPath currentFigure = renderer.getFigure();

        if (points != null) {
            for (int point = 0; point < numberOfPoints; point++) {

                currentFigure.lineTo(
                        (float) points[point].x,
                        (float) points[point].y);
            }
        }
    }
}
