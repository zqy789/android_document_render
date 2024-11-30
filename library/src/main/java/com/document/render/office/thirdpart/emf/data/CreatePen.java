

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class CreatePen extends EMFTag {

    private int index;

    private LogPen pen;

    public CreatePen() {
        super(38, 1);
    }

    public CreatePen(int index, LogPen pen) {
        this();
        this.index = index;
        this.pen = pen;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new CreatePen(emf.readDWORD(), new LogPen(emf));
    }

    public String toString() {
        return super.toString() + "\n  index: 0x" + Integer.toHexString(index) + "\n"
                + pen.toString();
    }


    public void render(EMFRenderer renderer) {













        renderer.storeGDIObject(index, pen);
    }
}
