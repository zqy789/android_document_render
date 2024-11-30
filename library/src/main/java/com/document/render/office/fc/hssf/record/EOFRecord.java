

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;


public final class EOFRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x0A;
    public static final int ENCODED_SIZE = 4;

    public static final EOFRecord instance = new EOFRecord();

    private EOFRecord() {

    }


    public EOFRecord(RecordInputStream in) {
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[EOF]\n");
        buffer.append("[/EOF]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
    }

    protected int getDataSize() {
        return ENCODED_SIZE - 4;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        return instance;
    }
}
