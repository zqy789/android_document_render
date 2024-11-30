

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class AbortPath extends EMFTag {

    public AbortPath() {
        super(68, 1);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return this;
    }
}
