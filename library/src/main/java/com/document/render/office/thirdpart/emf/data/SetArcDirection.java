

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFConstants;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class SetArcDirection extends EMFTag implements EMFConstants {

    private int direction;

    public SetArcDirection() {
        super(57, 1);
    }

    public SetArcDirection(int direction) {
        this();
        this.direction = direction;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new SetArcDirection(emf.readDWORD());
    }

    public String toString() {
        return super.toString() + "\n  direction: " + direction;
    }


    public void render(EMFRenderer renderer) {


        renderer.setArcDirection(direction);
    }
}
