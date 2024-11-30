

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.java.awt.geom.GeneralPath;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class StrokeAndFillPath extends EMFTag {

    private Rectangle bounds;

    public StrokeAndFillPath() {
        super(63, 1);
    }

    public StrokeAndFillPath(Rectangle bounds) {
        this();
        this.bounds = bounds;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new StrokeAndFillPath(emf.readRECTL());
    }


    public String toString() {
        return super.toString() + "\n  bounds: " + bounds;
    }


    public void render(EMFRenderer renderer) {
        GeneralPath currentPath = renderer.getPath();

        if (currentPath != null) {
            renderer.fillShape(currentPath);
            renderer.drawShape(currentPath);
            renderer.setPath(null);
        }
    }
}
