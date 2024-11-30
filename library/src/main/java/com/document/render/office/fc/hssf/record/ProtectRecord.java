

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class ProtectRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x0012;

    private static final BitField protectFlag = BitFieldFactory.getInstance(0x0001);

    private int _options;

    private ProtectRecord(int options) {
        _options = options;
    }

    public ProtectRecord(boolean isProtected) {
        this(0);
        setProtect(isProtected);
    }

    public ProtectRecord(RecordInputStream in) {
        this(in.readShort());
    }


    public boolean getProtect() {
        return protectFlag.isSet(_options);
    }


    public void setProtect(boolean protect) {
        _options = protectFlag.setBoolean(_options, protect);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[PROTECT]\n");
        buffer.append("    .options = ").append(HexDump.shortToHex(_options)).append("\n");
        buffer.append("[/PROTECT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(_options);
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        return new ProtectRecord(_options);
    }
}
