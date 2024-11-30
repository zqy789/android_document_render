

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class ProtectionRev4Record extends StandardRecord {
    @Keep
    public final static short sid = 0x01AF;

    private static final BitField protectedFlag = BitFieldFactory.getInstance(0x0001);

    private int _options;

    private ProtectionRev4Record(int options) {
        _options = options;
    }

    public ProtectionRev4Record(boolean protect) {
        this(0);
        setProtect(protect);
    }

    public ProtectionRev4Record(RecordInputStream in) {
        this(in.readUShort());
    }


    public boolean getProtect() {
        return protectedFlag.isSet(_options);
    }


    public void setProtect(boolean protect) {
        _options = protectedFlag.setBoolean(_options, protect);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[PROT4REV]\n");
        buffer.append("    .options = ").append(HexDump.shortToHex(_options)).append("\n");
        buffer.append("[/PROT4REV]\n");
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
}
