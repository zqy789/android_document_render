

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class EndSubRecord extends SubRecord {
    @Keep
    public final static short sid = 0x0000;
    private static final int ENCODED_SIZE = 0;

    public EndSubRecord() {

    }


    public EndSubRecord(LittleEndianInput in, int size) {
        if ((size & 0xFF) != ENCODED_SIZE) {
            throw new RecordFormatException("Unexpected size (" + size + ")");
        }
    }

    @Override
    public boolean isTerminating() {
        return true;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[ftEnd]\n");

        buffer.append("[/ftEnd]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(sid);
        out.writeShort(ENCODED_SIZE);
    }

    protected int getDataSize() {
        return ENCODED_SIZE;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        EndSubRecord rec = new EndSubRecord();

        return rec;
    }
}
