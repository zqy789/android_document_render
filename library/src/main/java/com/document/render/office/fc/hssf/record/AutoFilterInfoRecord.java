


package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;



public final class AutoFilterInfoRecord
        extends StandardRecord {
    @Keep
    public final static short sid = 0x9D;

    private short _cEntries;

    public AutoFilterInfoRecord() {
    }

    public AutoFilterInfoRecord(RecordInputStream in) {
        _cEntries = in.readShort();
    }



    public short getNumEntries() {
        return _cEntries;
    }



    public void setNumEntries(short num) {
        _cEntries = num;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[AUTOFILTERINFO]\n");
        buffer.append("    .numEntries          = ")
                .append(_cEntries).append("\n");
        buffer.append("[/AUTOFILTERINFO]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(_cEntries);
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    @Override
    public Object clone() {
        return cloneViaReserialise();
    }

}
