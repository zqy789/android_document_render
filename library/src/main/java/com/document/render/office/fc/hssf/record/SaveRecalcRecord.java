


package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;



public final class SaveRecalcRecord
        extends StandardRecord {
    @Keep
    public final static short sid = 0x5f;
    private short field_1_recalc;

    public SaveRecalcRecord() {
    }

    public SaveRecalcRecord(RecordInputStream in) {
        field_1_recalc = in.readShort();
    }



    public boolean getRecalc() {
        return (field_1_recalc == 1);
    }



    public void setRecalc(boolean recalc) {
        field_1_recalc = (short) ((recalc == true) ? 1
                : 0);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[SAVERECALC]\n");
        buffer.append("    .recalc         = ").append(getRecalc())
                .append("\n");
        buffer.append("[/SAVERECALC]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_recalc);
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        SaveRecalcRecord rec = new SaveRecalcRecord();
        rec.field_1_recalc = field_1_recalc;
        return rec;
    }
}
