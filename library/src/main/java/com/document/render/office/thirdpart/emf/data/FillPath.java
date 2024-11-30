

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.java.awt.geom.GeneralPath;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class FillPath extends EMFTag {

    private Rectangle bounds;

    public FillPath() {
        super(62, 1);
    }

    public FillPath(Rectangle bounds) {
        this();
        this.bounds = bounds;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new FillPath(emf.readRECTL());
    }

    public String toString() {
        return super.toString() + "\n  bounds: " + bounds;
    }


    public void render(EMFRenderer renderer) {
        GeneralPath currentPath = renderer.getPath();

        if (currentPath != null) {
            renderer.fillShape(currentPath);
            renderer.setPath(null);
        }
    }
}
