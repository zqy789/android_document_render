


package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;



public final class PrecisionRecord
        extends StandardRecord {
    @Keep
    public final static short sid = 0xE;
    public short field_1_precision;

    public PrecisionRecord() {
    }

    public PrecisionRecord(RecordInputStream in) {
        field_1_precision = in.readShort();
    }



    public boolean getFullPrecision() {
        return (field_1_precision == 1);
    }



    public void setFullPrecision(boolean fullprecision) {
        if (fullprecision == true) {
            field_1_precision = 1;
        } else {
            field_1_precision = 0;
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[PRECISION]\n");
        buffer.append("    .precision       = ").append(getFullPrecision())
                .append("\n");
        buffer.append("[/PRECISION]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_precision);
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }
}
