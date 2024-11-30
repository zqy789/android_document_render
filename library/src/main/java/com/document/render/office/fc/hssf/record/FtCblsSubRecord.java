

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class FtCblsSubRecord extends SubRecord {
    @Keep
    public final static short sid = 0x0C;
    private static final int ENCODED_SIZE = 20;

    private byte[] reserved;


    public FtCblsSubRecord() {
        reserved = new byte[ENCODED_SIZE];
    }

    public FtCblsSubRecord(LittleEndianInput in, int size) {
        if (size != ENCODED_SIZE) {
            throw new RecordFormatException("Unexpected size (" + size + ")");
        }

        byte[] buf = new byte[size];
        in.readFully(buf);
        reserved = buf;
    }


    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[FtCbls ]").append("\n");
        buffer.append("  size     = ").append(getDataSize()).append("\n");
        buffer.append("  reserved = ").append(HexDump.toHex(reserved)).append("\n");
        buffer.append("[/FtCbls ]").append("\n");
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
        FtCblsSubRecord rec = new FtCblsSubRecord();
        byte[] recdata = new byte[reserved.length];
        System.arraycopy(reserved, 0, recdata, 0, recdata.length);
        rec.reserved = recdata;
        return rec;
    }

}
