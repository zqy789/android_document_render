

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class SetMapperFlags extends EMFTag {

    private int flags;

    public SetMapperFlags() {
        super(16, 1);
    }

    public SetMapperFlags(int flags) {
        this();
        this.flags = flags;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new SetMapperFlags(emf.readDWORD());
    }


    public String toString() {
        return super.toString() + "\n  flags: " + flags;
    }
}
