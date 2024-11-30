

package com.document.render.office.fc.hssf.record;

import com.document.render.office.fc.util.LittleEndianByteArrayOutputStream;
import com.document.render.office.fc.util.LittleEndianOutput;


public abstract class StandardRecord extends Record {
    protected abstract int getDataSize();

    public final int getRecordSize() {
        return 4 + getDataSize();
    }

    @Override
    public final int serialize(int offset, byte[] data) {
        int dataSize = getDataSize();
        int recSize = 4 + dataSize;
        LittleEndianByteArrayOutputStream out = new LittleEndianByteArrayOutputStream(data, offset, recSize);
        out.writeShort(getSid());
        out.writeShort(dataSize);
        serialize(out);
        if (out.getWriteIndex() - offset != recSize) {
            throw new IllegalStateException("Error in serialization of (" + getClass().getName() + "): "
                    + "Incorrect number of bytes written - expected "
                    + recSize + " but got " + (out.getWriteIndex() - offset));
        }
        return recSize;
    }


    protected abstract void serialize(LittleEndianOutput out);
}
