

package com.document.render.office.fc.hwpf.model;

import com.document.render.office.fc.util.Internal;

import java.text.MessageFormat;



@Internal
public class PlexOfField {

    private final GenericPropertyNode propertyNode;
    private final FieldDescriptor fld;

    @Deprecated
    public PlexOfField(int fcStart, int fcEnd, byte[] data) {
        propertyNode = new GenericPropertyNode(fcStart, fcEnd, data);
        fld = new FieldDescriptor(data);
    }

    public PlexOfField(GenericPropertyNode propertyNode) {
        this.propertyNode = propertyNode;
        fld = new FieldDescriptor(propertyNode.getBytes());
    }

    public int getFcStart() {
        return propertyNode.getStart();
    }

    public int getFcEnd() {
        return propertyNode.getEnd();
    }

    public FieldDescriptor getFld() {
        return fld;
    }

    public String toString() {
        return MessageFormat.format("[{0}, {1}) - FLD - 0x{2}; 0x{3}",
                getFcStart(), getFcEnd(),
                Integer.toHexString(0xff & fld.getBoundaryType()),
                Integer.toHexString(0xff & fld.getFlt()));
    }
}
