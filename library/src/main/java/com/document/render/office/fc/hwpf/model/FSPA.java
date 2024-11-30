

package com.document.render.office.fc.hwpf.model;

import com.document.render.office.fc.hwpf.model.types.FSPAAbstractType;
import com.document.render.office.fc.util.Internal;



@Internal
public final class FSPA extends FSPAAbstractType {
    @Deprecated
    public static final int FSPA_SIZE = getSize();

    public FSPA() {
    }

    public FSPA(byte[] bytes, int offset) {
        fillFields(bytes, offset);
    }

    public byte[] toByteArray() {
        byte[] buf = new byte[FSPA_SIZE];
        serialize(buf, 0);
        return buf;
    }

}
