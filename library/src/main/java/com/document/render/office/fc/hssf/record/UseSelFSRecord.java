

package com.document.render.office.fc.hssf.record;

import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class UseSelFSRecord extends StandardRecord {
    public final static short sid = 0x0160;

    private static final BitField useNaturalLanguageFormulasFlag = BitFieldFactory.getInstance(0x0001);

    private int _options;

    private UseSelFSRecord(int options) {
        _options = options;
    }

    public UseSelFSRecord(RecordInputStream in) {
        this(in.readUShort());
    }

    public UseSelFSRecord(boolean b) {
        this(0);
        _options = useNaturalLanguageFormulasFlag.setBoolean(_options, b);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[USESELFS]\n");
        buffer.append("    .options = ").append(HexDump.shortToHex(_options)).append("\n");
        buffer.append("[/USESELFS]\n");
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

    @Override
    public Object clone() {
        return new UseSelFSRecord(_options);
    }
}
