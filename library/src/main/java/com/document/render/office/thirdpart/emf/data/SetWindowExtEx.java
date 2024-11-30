

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.Dimension;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class SetWindowExtEx extends EMFTag {

    private Dimension size;

    public SetWindowExtEx() {
        super(9, 1);
    }

    public SetWindowExtEx(Dimension size) {
        this();
        this.size = size;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new SetWindowExtEx(emf.readSIZEL());
    }

    public String toString() {
        return super.toString() + "\n  size: " + size;
    }


    public void render(EMFRenderer renderer) {
        renderer.setWindowSize(size);
    }
}
