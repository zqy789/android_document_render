

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class ResizePalette extends EMFTag {

    private int index, entries;

    public ResizePalette() {
        super(51, 1);
    }

    public ResizePalette(int index, int entries) {
        this();
        this.index = index;
        this.entries = entries;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new ResizePalette(emf.readDWORD(), emf.readDWORD());
    }

    public String toString() {
        return super.toString() + "\n  index: 0x" + Integer.toHexString(index) + "\n  entries: "
                + entries;
    }
}
