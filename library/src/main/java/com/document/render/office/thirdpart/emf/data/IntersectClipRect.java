

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class IntersectClipRect extends EMFTag {

    private Rectangle bounds;

    public IntersectClipRect() {
        super(30, 1);
    }

    public IntersectClipRect(Rectangle bounds) {
        this();
        this.bounds = bounds;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new IntersectClipRect(emf.readRECTL());
    }

    public String toString() {
        return super.toString() + "\n  bounds: " + bounds;
    }


    public void render(EMFRenderer renderer) {



        renderer.clip(bounds);
    }
}
