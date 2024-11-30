


package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;



public final class CalcCountRecord
        extends StandardRecord {
    @Keep
    public final static short sid = 0xC;
    private short field_1_iterations;

    public CalcCountRecord() {
    }

    public CalcCountRecord(RecordInputStream in) {
        field_1_iterations = in.readShort();
    }



    public short getIterations() {
        return field_1_iterations;
    }



    public void setIterations(short iterations) {
        field_1_iterations = iterations;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[CALCCOUNT]\n");
        buffer.append("    .iterations     = ")
                .append(Integer.toHexString(getIterations())).append("\n");
        buffer.append("[/CALCCOUNT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getIterations());
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        CalcCountRecord rec = new CalcCountRecord();
        rec.field_1_iterations = field_1_iterations;
        return rec;
    }
}
