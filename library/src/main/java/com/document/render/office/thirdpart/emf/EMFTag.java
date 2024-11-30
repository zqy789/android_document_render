

package com.document.render.office.thirdpart.emf;

import com.document.render.office.thirdpart.emf.data.GDIObject;
import com.document.render.office.thirdpart.emf.io.Tag;
import com.document.render.office.thirdpart.emf.io.TaggedInputStream;

import java.io.IOException;


public abstract class EMFTag extends Tag implements GDIObject {


    protected EMFTag(int id, int version) {
        super(id, version);
    }

    public Tag read(int tagID, TaggedInputStream input, int len) throws IOException {

        return read(tagID, (EMFInputStream) input, len);
    }

    public abstract EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException;


    public String toString() {
        return "EMFTag " + getName() + " (" + getTag() + ")";
    }


    public void render(EMFRenderer renderer) {
    }
}
