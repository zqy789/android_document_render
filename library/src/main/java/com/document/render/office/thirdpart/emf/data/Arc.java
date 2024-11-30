
package com.document.render.office.thirdpart.emf.data;

import android.graphics.Point;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.java.awt.geom.Arc2D;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class Arc extends AbstractArc {

    public Arc() {
        super(45, 1, null, null, null);
    }

    public Arc(Rectangle bounds, Point start, Point end) {
        super(45, 1, bounds, start, end);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new Arc(
                emf.readRECTL(),
                emf.readPOINTL(),
                emf.readPOINTL());
    }


    public void render(EMFRenderer renderer) {



























        renderer.fillAndDrawOrAppend(
                getShape(renderer, Arc2D.OPEN));
    }
}
