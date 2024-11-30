

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.java.awt.geom.Ellipse2D;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class Ellipse extends EMFTag {
    private Rectangle bounds;

    public Ellipse(Rectangle bounds) {
        this();
        this.bounds = bounds;
    }

    public Ellipse() {
        super(42, 1);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new Ellipse(emf.readRECTL());
    }

    public String toString() {
        return super.toString() + "\n  bounds: " + bounds;
    }


    public void render(EMFRenderer renderer) {





        renderer.fillAndDrawOrAppend(new Ellipse2D.Double(bounds.x, bounds.y, bounds
                .getWidth(), bounds.getHeight()));
    }
}
