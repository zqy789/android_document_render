

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;


public final class UncalcedRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x005E;

    private short _reserved;

    public UncalcedRecord() {
        _reserved = 0;
    }

    public UncalcedRecord(RecordInputStream in) {
        _reserved = in.readShort();
    }

    public static int getStaticRecordSize() {
        return 6;
    }

    public short getSid() {
        return sid;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[UNCALCED]\n");
        buffer.append("    _reserved: ").append(_reserved).append('\n');
        buffer.append("[/UNCALCED]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(_reserved);
    }

    protected int getDataSize() {
        return 2;
    }
}
