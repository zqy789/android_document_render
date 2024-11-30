
package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.geom.AffineTransform;
import com.document.render.office.java.awt.geom.GeneralPath;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class BeginPath extends EMFTag {

    public BeginPath() {
        super(59, 1);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return this;
    }


    public void render(EMFRenderer renderer) {


        renderer.setPath(new GeneralPath(
                renderer.getWindingRule()));
        renderer.setPathTransform(new AffineTransform());
    }
}
