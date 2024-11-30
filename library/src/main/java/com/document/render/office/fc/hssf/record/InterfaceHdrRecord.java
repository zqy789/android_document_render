

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class InterfaceHdrRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x00E1;

    public final static int CODEPAGE = 0x04B0;
    private final int _codepage;

    public InterfaceHdrRecord(int codePage) {
        _codepage = codePage;
    }

    public InterfaceHdrRecord(RecordInputStream in) {
        _codepage = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[INTERFACEHDR]\n");
        buffer.append("    .codepage = ").append(HexDump.shortToHex(_codepage)).append("\n");
        buffer.append("[/INTERFACEHDR]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(_codepage);
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }
}
