


package com.document.render.office.fc.hssf.record;

import com.document.render.office.fc.util.LittleEndianOutput;



public final class CalcModeRecord
        extends StandardRecord {
    public final static short sid = 0xD;



    public final static short MANUAL = 0;



    public final static short AUTOMATIC = 1;



    public final static short AUTOMATIC_EXCEPT_TABLES = -1;
    private short field_1_calcmode;

    public CalcModeRecord() {
    }

    public CalcModeRecord(RecordInputStream in) {
        field_1_calcmode = in.readShort();
    }



    public short getCalcMode() {
        return field_1_calcmode;
    }



    public void setCalcMode(short calcmode) {
        field_1_calcmode = calcmode;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[CALCMODE]\n");
        buffer.append("    .calcmode       = ")
                .append(Integer.toHexString(getCalcMode())).append("\n");
        buffer.append("[/CALCMODE]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getCalcMode());
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        CalcModeRecord rec = new CalcModeRecord();
        rec.field_1_calcmode = field_1_calcmode;
        return rec;
    }
}
