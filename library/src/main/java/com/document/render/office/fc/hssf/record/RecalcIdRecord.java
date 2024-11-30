

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class RecalcIdRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x01C1;
    private final int _reserved0;


    private int _engineId;

    public RecalcIdRecord() {
        _reserved0 = 0;
        _engineId = 0;
    }

    public RecalcIdRecord(RecordInputStream in) {
        in.readUShort();
        _reserved0 = in.readUShort();
        _engineId = in.readInt();
    }

    public boolean isNeeded() {
        return true;
    }

    public int getEngineId() {
        return _engineId;
    }

    public void setEngineId(int val) {
        _engineId = val;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[RECALCID]\n");
        buffer.append("    .reserved = ").append(HexDump.shortToHex(_reserved0)).append("\n");
        buffer.append("    .engineId = ").append(HexDump.intToHex(_engineId)).append("\n");
        buffer.append("[/RECALCID]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(sid);
        out.writeShort(_reserved0);
        out.writeInt(_engineId);
    }

    protected int getDataSize() {
        return 8;
    }

    public short getSid() {
        return sid;
    }
}
