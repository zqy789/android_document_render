

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;


public final class DefaultColWidthRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x0055;

    public final static int DEFAULT_COLUMN_WIDTH = 0x0008;
    private int field_1_col_width;

    public DefaultColWidthRecord() {
        field_1_col_width = DEFAULT_COLUMN_WIDTH;
    }

    public DefaultColWidthRecord(RecordInputStream in) {
        field_1_col_width = in.readUShort();
    }



    public int getColWidth() {
        return field_1_col_width;
    }



    public void setColWidth(int width) {
        field_1_col_width = width;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[DEFAULTCOLWIDTH]\n");
        buffer.append("    .colwidth      = ")
                .append(Integer.toHexString(getColWidth())).append("\n");
        buffer.append("[/DEFAULTCOLWIDTH]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getColWidth());
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        DefaultColWidthRecord rec = new DefaultColWidthRecord();
        rec.field_1_col_width = field_1_col_width;
        return rec;
    }
}
