

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;


public final class TopMarginRecord extends StandardRecord implements Margin {
    @Keep
    public final static short sid = 0x28;
    private double field_1_margin;

    public TopMarginRecord() {
    }


    public TopMarginRecord(RecordInputStream in) {
        field_1_margin = in.readDouble();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[TopMargin]\n");
        buffer.append("    .margin               = ").append(" (").append(getMargin()).append(" )\n");
        buffer.append("[/TopMargin]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeDouble(field_1_margin);
    }

    protected int getDataSize() {
        return 8;
    }

    public short getSid() {
        return sid;
    }


    public double getMargin() {
        return field_1_margin;
    }


    public void setMargin(double field_1_margin) {
        this.field_1_margin = field_1_margin;
    }

    public Object clone() {
        TopMarginRecord rec = new TopMarginRecord();
        rec.field_1_margin = this.field_1_margin;
        return rec;
    }
}
