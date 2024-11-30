

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class EOF extends EMFTag {

    public EOF() {
        super(14, 1);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {


        emf.readUnsignedByte(len);
        return new EOF();
    }


    public void render(EMFRenderer renderer) {

    }
}
