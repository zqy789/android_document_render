

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class ContinueRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x003C;
    private byte[] _data;

    public ContinueRecord(byte[] data) {
        _data = data;
    }

    public ContinueRecord(RecordInputStream in) {
        _data = in.readRemainder();
    }

    protected int getDataSize() {
        if (_data != null) {
            return _data.length;
        }

        return 0;
    }

    public void serialize(LittleEndianOutput out) {
        out.write(_data);
    }


    public byte[] getData() {
        return _data;
    }

    public void resetData() {
        _data = null;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[CONTINUE RECORD]\n");
        buffer.append("    .data = ").append(HexDump.toHex(_data)).append("\n");
        buffer.append("[/CONTINUE RECORD]\n");
        return buffer.toString();
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        return new ContinueRecord(_data);
    }
}
