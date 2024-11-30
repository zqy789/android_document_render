

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class SetMiterLimit extends EMFTag {

    private int limit;

    public SetMiterLimit() {
        super(58, 1);
    }

    public SetMiterLimit(int limit) {
        this();
        this.limit = limit;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new SetMiterLimit(emf.readDWORD());
    }

    public String toString() {
        return super.toString() + "\n  limit: " + limit;
    }


    public void render(EMFRenderer renderer) {








        renderer.setMeterLimit(limit);
    }
}
