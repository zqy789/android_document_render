

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class IterationRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x0011;

    private static final BitField iterationOn = BitFieldFactory.getInstance(0x0001);

    private int _flags;

    public IterationRecord(boolean iterateOn) {
        _flags = iterationOn.setBoolean(0, iterateOn);
    }

    public IterationRecord(RecordInputStream in) {
        _flags = in.readShort();
    }


    public boolean getIteration() {
        return iterationOn.isSet(_flags);
    }


    public void setIteration(boolean iterate) {
        _flags = iterationOn.setBoolean(_flags, iterate);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[ITERATION]\n");
        buffer.append("    .flags      = ").append(HexDump.shortToHex(_flags)).append("\n");
        buffer.append("[/ITERATION]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(_flags);
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        return new IterationRecord(getIteration());
    }
}
