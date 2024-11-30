
package com.document.render.office.thirdpart.emf.data;

import android.graphics.Point;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.java.awt.geom.GeneralPath;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class PolyBezierTo extends AbstractPolygon {

    public PolyBezierTo() {
        super(5, 1, null, 0, null);
    }

    public PolyBezierTo(Rectangle bounds, int numberOfPoints, Point[] points) {
        super(5, 1, bounds, numberOfPoints, points);
    }

    protected PolyBezierTo(int id, int version, Rectangle bounds, int numberOfPoints, Point[] points) {
        super(id, version, bounds, numberOfPoints, points);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        Rectangle r = emf.readRECTL();
        int n = emf.readDWORD();
        return new PolyBezierTo(r, n, emf.readPOINTL(n));
    }


    public void render(EMFRenderer renderer) {
        Point[] points = getPoints();
        int numberOfPoints = getNumberOfPoints();
        GeneralPath currentFigure = renderer.getFigure();

        if (points != null && points.length > 0) {

            Point p1, p2, p3;
            for (int point = 0; point < numberOfPoints; point = point + 3) {

                p1 = points[point];
                p2 = points[point + 1];
                p3 = points[point + 2];
                currentFigure.curveTo(
                        (float) p1.x, (float) p1.y,
                        (float) p2.x, (float) p2.y,
                        (float) p3.x, (float) p3.y);
            }
        }
    }
}
