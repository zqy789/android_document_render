
package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.thirdpart.emf.EMFConstants;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class ExtTextOutW extends AbstractExtTextOut implements EMFConstants {

    private TextW text;

    public ExtTextOutW() {
        super(84, 1, null, 0, 1, 1);
    }

    public ExtTextOutW(
            Rectangle bounds,
            int mode,
            float xScale,
            float yScale,
            TextW text) {

        super(84, 1, bounds, mode, xScale, yScale);
        this.text = text;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new ExtTextOutW(
                emf.readRECTL(),
                emf.readDWORD(),
                emf.readFLOAT(),
                emf.readFLOAT(),
                TextW.read(emf));
    }

    public Text getText() {
        return text;
    }
}