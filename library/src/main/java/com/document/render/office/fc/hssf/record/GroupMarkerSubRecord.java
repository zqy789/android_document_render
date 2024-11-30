

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class GroupMarkerSubRecord extends SubRecord {
    @Keep
    public final static short sid = 0x0006;

    private static final byte[] EMPTY_BYTE_ARRAY = {};

    private byte[] reserved;

    public GroupMarkerSubRecord() {
        reserved = EMPTY_BYTE_ARRAY;
    }

    public GroupMarkerSubRecord(LittleEndianInput in, int size) {
        byte[] buf = new byte[size];
        in.readFully(buf);
        reserved = buf;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        String nl = System.getProperty("line.separator");
        buffer.append("[ftGmo]" + nl);
        buffer.append("  reserved = ").append(HexDump.toHex(reserved)).append(nl);
        buffer.append("[/ftGmo]" + nl);
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(sid);
        out.writeShort(reserved.length);
        out.write(reserved);
    }

    protected int getDataSize() {
        return reserved.length;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        GroupMarkerSubRecord rec = new GroupMarkerSubRecord();
        rec.reserved = new byte[reserved.length];
        for (int i = 0; i < reserved.length; i++)
            rec.reserved[i] = reserved[i];
        return rec;
    }
}
