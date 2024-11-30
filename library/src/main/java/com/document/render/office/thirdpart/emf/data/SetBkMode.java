

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFConstants;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class SetBkMode extends EMFTag implements EMFConstants {

    private int mode;

    public SetBkMode() {
        super(18, 1);
    }

    public SetBkMode(int mode) {
        this();
        this.mode = mode;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new SetBkMode(emf.readDWORD());
    }

    public String toString() {
        return super.toString() + "\n  mode: " + mode;
    }


    public void render(EMFRenderer renderer) {



        renderer.setBkMode(mode);
    }
}
