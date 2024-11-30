
package com.document.render.office.thirdpart.emf.data;

import android.graphics.Point;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.java.awt.geom.GeneralPath;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;


public abstract class AbstractPolyPolygon extends EMFTag {

    private Rectangle bounds;

    private int[] numberOfPoints;

    private Point[][] points;


    protected AbstractPolyPolygon(
            int id, int version,
            Rectangle bounds,
            int[] numberOfPoints,
            Point[][] points) {

        super(id, version);
        this.bounds = bounds;
        this.numberOfPoints = numberOfPoints;
        this.points = points;
    }

    public String toString() {
        return super.toString() +
                "\n  bounds: " + bounds +
                "\n  #polys: " + numberOfPoints.length;
    }

    protected Rectangle getBounds() {
        return bounds;
    }

    protected int[] getNumberOfPoints() {
        return numberOfPoints;
    }

    protected Point[][] getPoints() {
        return points;
    }


    public void render(EMFRenderer renderer) {
        render(renderer, true);
    }


    protected void render(EMFRenderer renderer, boolean closePath) {

        GeneralPath path = new GeneralPath(
                renderer.getWindingRule());


        Point p;
        for (int polygon = 0; polygon < numberOfPoints.length; polygon++) {


            GeneralPath gp = new GeneralPath(
                    renderer.getWindingRule());
            for (int point = 0; point < numberOfPoints[polygon]; point++) {

                p = points[polygon][point];
                if (point > 0) {
                    gp.lineTo((float) p.x, (float) p.y);
                } else {
                    gp.moveTo((float) p.x, (float) p.y);
                }
            }


            if (closePath) {
                gp.closePath();
            }

            path.append(gp, false);
        }


        if (closePath) {
            renderer.fillAndDrawOrAppend(path);
        } else {
            renderer.drawOrAppend(path);
        }
    }
}
