

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.Dimension;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.java.awt.geom.RoundRectangle2D;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class RoundRect extends EMFTag {

    private Rectangle bounds;

    private Dimension corner;

    public RoundRect() {
        super(44, 1);
    }

    public RoundRect(Rectangle bounds, Dimension corner) {
        this();
        this.bounds = bounds;
        this.corner = corner;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new RoundRect(emf.readRECTL(), emf.readSIZEL());
    }

    public String toString() {
        return super.toString() + "\n  bounds: " + bounds + "\n  corner: " + corner;
    }


    public void render(EMFRenderer renderer) {
        renderer.fillAndDrawOrAppend(new RoundRectangle2D.Double(bounds.x, bounds.x,
                bounds.getWidth(), bounds.getHeight(), corner.getWidth(), corner.getHeight()));
    }
}
