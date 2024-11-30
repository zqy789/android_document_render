

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.Image;
import com.document.render.office.thirdpart.emf.EMFConstants;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class SetStretchBltMode extends EMFTag implements EMFConstants {

    private int mode;

    public SetStretchBltMode() {
        super(21, 1);
    }

    public SetStretchBltMode(int mode) {
        this();
        this.mode = mode;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new SetStretchBltMode(emf.readDWORD());
    }


    public String toString() {
        return super.toString() + "\n  mode: " + mode;
    }


    public void render(EMFRenderer renderer) {



        renderer.setScaleMode(getScaleMode(mode));
    }


    private int getScaleMode(int mode) {


        if (mode == EMFConstants.COLORONCOLOR ) {
            return Image.SCALE_FAST;
        }



        else if (mode == EMFConstants.HALFTONE ) {
            return Image.SCALE_SMOOTH;
        }




        else if (mode == EMFConstants.BLACKONWHITE ) {

            return Image.SCALE_REPLICATE;
        }




        else if (mode == EMFConstants.WHITEONBLACK ) {

            return Image.SCALE_REPLICATE;
        } else {
            return Image.SCALE_DEFAULT;
        }
    }
}
