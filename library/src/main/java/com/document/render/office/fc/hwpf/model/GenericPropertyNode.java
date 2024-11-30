

package com.document.render.office.fc.hwpf.model;

import com.document.render.office.fc.util.Internal;

@Internal
public final class GenericPropertyNode extends PropertyNode<GenericPropertyNode> {
    public GenericPropertyNode(int start, int end, byte[] buf) {
        super(start, end, buf);
    }

    public byte[] getBytes() {
        return (byte[]) _buf;
    }

    @Override
    public String toString() {
        return "GenericPropertyNode [" + getStart() + "; " + getEnd() + ") "
                + (getBytes() != null ? getBytes().length + " byte(s)" : "null");
    }
}
