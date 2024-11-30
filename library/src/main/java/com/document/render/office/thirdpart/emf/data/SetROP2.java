

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFConstants;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class SetROP2 extends EMFTag implements EMFConstants {

    private int mode;

    public SetROP2() {
        super(20, 1);
    }

    public SetROP2(int mode) {
        this();
        this.mode = mode;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new SetROP2(emf.readDWORD());
    }


    public String toString() {
        return super.toString() + "\n  mode: " + mode;
    }


    public void render(EMFRenderer renderer) {





        renderer.setRop2(mode);
    }
}
