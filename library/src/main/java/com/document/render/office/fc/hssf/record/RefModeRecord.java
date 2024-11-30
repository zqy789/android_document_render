


package com.document.render.office.fc.hssf.record;

import com.document.render.office.fc.util.LittleEndianOutput;



public final class RefModeRecord
        extends StandardRecord {
    public final static short sid = 0xf;
    public final static short USE_A1_MODE = 1;
    public final static short USE_R1C1_MODE = 0;
    private short field_1_mode;

    public RefModeRecord() {
    }

    public RefModeRecord(RecordInputStream in) {
        field_1_mode = in.readShort();
    }



    public short getMode() {
        return field_1_mode;
    }



    public void setMode(short mode) {
        field_1_mode = mode;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[REFMODE]\n");
        buffer.append("    .mode           = ")
                .append(Integer.toHexString(getMode())).append("\n");
        buffer.append("[/REFMODE]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getMode());
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        RefModeRecord rec = new RefModeRecord();
        rec.field_1_mode = field_1_mode;
        return rec;
    }
}
