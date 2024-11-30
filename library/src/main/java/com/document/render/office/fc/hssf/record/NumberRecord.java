

package com.document.render.office.fc.hssf.record;


import androidx.annotation.Keep;

import com.document.render.office.fc.ss.util.NumberToTextConverter;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class NumberRecord extends CellRecord {
    @Keep
    public static final short sid = 0x0203;
    private double field_4_value;


    public NumberRecord() {

    }


    public NumberRecord(RecordInputStream in) {
        super(in);
        field_4_value = in.readDouble();
    }


    public double getValue() {
        return field_4_value;
    }


    public void setValue(double value) {
        field_4_value = value;
    }

    @Override
    protected String getRecordName() {
        return "NUMBER";
    }

    @Override
    protected void appendValueText(StringBuilder sb) {
        sb.append("  .value= ").append(NumberToTextConverter.toText(field_4_value));
    }

    @Override
    protected void serializeValue(LittleEndianOutput out) {
        out.writeDouble(getValue());
    }

    @Override
    protected int getValueDataSize() {
        return 8;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        NumberRecord rec = new NumberRecord();
        copyBaseFields(rec);
        rec.field_4_value = field_4_value;
        return rec;
    }
}
