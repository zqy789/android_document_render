

package com.document.render.office.thirdpart.emf.data;

import android.graphics.Point;

import com.document.render.office.java.awt.geom.GeneralPath;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class MoveToEx extends EMFTag {

    private Point point;

    public MoveToEx() {
        super(27, 1);
    }

    public MoveToEx(Point point) {
        this();
        this.point = point;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new MoveToEx(emf.readPOINTL());
    }

    public String toString() {
        return super.toString() + "\n  point: " + point;
    }


    public void render(EMFRenderer renderer) {



        GeneralPath currentFigure = new GeneralPath(renderer.getWindingRule());
        currentFigure.moveTo((float) point.x, (float) point.y);
        renderer.setFigure(currentFigure);
    }
}
