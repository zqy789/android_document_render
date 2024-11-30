
package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class CloseFigure extends EMFTag {

    public CloseFigure() {
        super(61, 1);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return this;
    }


    public void render(EMFRenderer renderer) {
        renderer.closeFigure();
    }
}
