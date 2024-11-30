

package com.document.render.office.fc.hssf.record;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class SCLRecord extends StandardRecord {
    public final static short sid = 0x00A0;
    private short field_1_numerator;
    private short field_2_denominator;


    public SCLRecord() {

    }

    public SCLRecord(RecordInputStream in) {
        field_1_numerator = in.readShort();
        field_2_denominator = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[SCL]\n");
        buffer.append("    .numerator            = ")
                .append("0x").append(HexDump.toHex(getNumerator()))
                .append(" (").append(getNumerator()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .denominator          = ")
                .append("0x").append(HexDump.toHex(getDenominator()))
                .append(" (").append(getDenominator()).append(" )");
        buffer.append(System.getProperty("line.separator"));

        buffer.append("[/SCL]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_numerator);
        out.writeShort(field_2_denominator);
    }

    protected int getDataSize() {
        return 2 + 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        SCLRecord rec = new SCLRecord();

        rec.field_1_numerator = field_1_numerator;
        rec.field_2_denominator = field_2_denominator;
        return rec;
    }



    public short getNumerator() {
        return field_1_numerator;
    }


    public void setNumerator(short field_1_numerator) {
        this.field_1_numerator = field_1_numerator;
    }


    public short getDenominator() {
        return field_2_denominator;
    }


    public void setDenominator(short field_2_denominator) {
        this.field_2_denominator = field_2_denominator;
    }
}
