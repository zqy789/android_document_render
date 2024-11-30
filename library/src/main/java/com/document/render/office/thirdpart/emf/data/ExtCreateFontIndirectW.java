

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class ExtCreateFontIndirectW extends EMFTag {

    private int index;

    private ExtLogFontW font;

    public ExtCreateFontIndirectW() {
        super(82, 1);
    }

    public ExtCreateFontIndirectW(int index, ExtLogFontW font) {
        this();
        this.index = index;
        this.font = font;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new ExtCreateFontIndirectW(emf.readDWORD(), new ExtLogFontW(emf));
    }

    public String toString() {
        return super.toString() + "\n  index: 0x" + Integer.toHexString(index) + "\n"
                + font.toString();
    }


    public void render(EMFRenderer renderer) {
        renderer.storeGDIObject(index, font);
    }
}
