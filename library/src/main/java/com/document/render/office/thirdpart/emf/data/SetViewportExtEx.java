

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.Dimension;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class SetViewportExtEx extends EMFTag {

    private Dimension size;

    public SetViewportExtEx() {
        super(11, 1);
    }

    public SetViewportExtEx(Dimension size) {
        this();
        this.size = size;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new SetViewportExtEx(emf.readSIZEL());
    }

    public String toString() {
        return super.toString() + "\n  size: " + size;
    }





    public void render(EMFRenderer renderer) {






        renderer.setViewportSize(size);
    }
}
