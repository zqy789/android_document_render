

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.geom.AffineTransform;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class SetWorldTransform extends EMFTag {

    private AffineTransform transform;

    public SetWorldTransform() {
        super(35, 1);
    }

    public SetWorldTransform(AffineTransform transform) {
        this();
        this.transform = transform;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new SetWorldTransform(emf.readXFORM());
    }

    public String toString() {
        return super.toString() + "\n  transform: " + transform;
    }


    public void render(EMFRenderer renderer) {
        if (renderer.getPath() != null) {
            renderer.setPathTransform(transform);
        } else {
            renderer.resetTransformation();
            renderer.transform(transform);
        }
    }
}
