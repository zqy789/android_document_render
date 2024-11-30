

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class ExcludeClipRect extends EMFTag {

    private Rectangle bounds;

    public ExcludeClipRect() {
        super(29, 1);
    }

    public ExcludeClipRect(Rectangle bounds) {
        this();
        this.bounds = bounds;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new ExcludeClipRect(emf.readRECTL());
    }

    public String toString() {
        return super.toString() + "\n  bounds: " + bounds;
    }
}
