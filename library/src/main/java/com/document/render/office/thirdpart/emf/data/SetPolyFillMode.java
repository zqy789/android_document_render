

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.geom.GeneralPath;
import com.document.render.office.thirdpart.emf.EMFConstants;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class SetPolyFillMode extends EMFTag implements EMFConstants {

    private int mode;

    public SetPolyFillMode() {
        super(19, 1);
    }

    public SetPolyFillMode(int mode) {
        this();
        this.mode = mode;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new SetPolyFillMode(emf.readDWORD());
    }


    public String toString() {
        return super.toString() + "\n  mode: " + mode;
    }


    public void render(EMFRenderer renderer) {
        renderer.setWindingRule(getWindingRule(mode));
    }


    private int getWindingRule(int polyFillMode) {
        if (polyFillMode == EMFConstants.WINDING) {
            return GeneralPath.WIND_EVEN_ODD;
        } else if (polyFillMode == EMFConstants.ALTERNATE) {
            return GeneralPath.WIND_NON_ZERO;
        } else {
            return GeneralPath.WIND_EVEN_ODD;
        }
    }

}
