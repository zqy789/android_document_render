

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class SelectPalette extends EMFTag {

    private int index;

    public SelectPalette() {
        super(48, 1);
    }

    public SelectPalette(int index) {
        this();
        this.index = index;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new SelectPalette(emf.readDWORD());
    }

    public String toString() {
        return super.toString() + "\n  index: 0x" + Integer.toHexString(index);
    }


    public void render(EMFRenderer renderer) {




    }
}
