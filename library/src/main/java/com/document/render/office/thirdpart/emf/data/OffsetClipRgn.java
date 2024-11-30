

package com.document.render.office.thirdpart.emf.data;

import android.graphics.Point;

import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class OffsetClipRgn extends EMFTag {

    private Point offset;

    public OffsetClipRgn() {
        super(26, 1);
    }

    public OffsetClipRgn(Point offset) {
        this();
        this.offset = offset;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new OffsetClipRgn(emf.readPOINTL());
    }

    public String toString() {
        return super.toString() + "\n  offset: " + offset;
    }
}
