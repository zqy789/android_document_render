

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class RefreshAllRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x01B7;

    private static final BitField refreshFlag = BitFieldFactory.getInstance(0x0001);

    private int _options;

    private RefreshAllRecord(int options) {
        _options = options;
    }

    public RefreshAllRecord(RecordInputStream in) {
        this(in.readUShort());
    }

    public RefreshAllRecord(boolean refreshAll) {
        this(0);
        setRefreshAll(refreshAll);
    }


    public boolean getRefreshAll() {
        return refreshFlag.isSet(_options);
    }


    public void setRefreshAll(boolean refreshAll) {
        _options = refreshFlag.setBoolean(_options, refreshAll);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[REFRESHALL]\n");
        buffer.append("    .options      = ").append(HexDump.shortToHex(_options)).append("\n");
        buffer.append("[/REFRESHALL]\n");
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
        return new RefreshAllRecord(_options);
    }
}
