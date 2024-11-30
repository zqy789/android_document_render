


package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;



public final class GutsRecord
        extends StandardRecord {
    @Keep
    public final static short sid = 0x80;
    private short field_1_left_row_gutter;
    private short field_2_top_col_gutter;
    private short field_3_row_level_max;
    private short field_4_col_level_max;

    public GutsRecord() {
    }

    public GutsRecord(RecordInputStream in) {
        field_1_left_row_gutter = in.readShort();
        field_2_top_col_gutter = in.readShort();
        field_3_row_level_max = in.readShort();
        field_4_col_level_max = in.readShort();
    }



    public short getLeftRowGutter() {
        return field_1_left_row_gutter;
    }



    public void setLeftRowGutter(short gut) {
        field_1_left_row_gutter = gut;
    }



    public short getTopColGutter() {
        return field_2_top_col_gutter;
    }



    public void setTopColGutter(short gut) {
        field_2_top_col_gutter = gut;
    }



    public short getRowLevelMax() {
        return field_3_row_level_max;
    }



    public void setRowLevelMax(short max) {
        field_3_row_level_max = max;
    }



    public short getColLevelMax() {
        return field_4_col_level_max;
    }



    public void setColLevelMax(short max) {
        field_4_col_level_max = max;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[GUTS]\n");
        buffer.append("    .leftgutter     = ")
                .append(Integer.toHexString(getLeftRowGutter())).append("\n");
        buffer.append("    .topgutter      = ")
                .append(Integer.toHexString(getTopColGutter())).append("\n");
        buffer.append("    .rowlevelmax    = ")
                .append(Integer.toHexString(getRowLevelMax())).append("\n");
        buffer.append("    .collevelmax    = ")
                .append(Integer.toHexString(getColLevelMax())).append("\n");
        buffer.append("[/GUTS]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getLeftRowGutter());
        out.writeShort(getTopColGutter());
        out.writeShort(getRowLevelMax());
        out.writeShort(getColLevelMax());
    }

    protected int getDataSize() {
        return 8;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        GutsRecord rec = new GutsRecord();
        rec.field_1_left_row_gutter = field_1_left_row_gutter;
        rec.field_2_top_col_gutter = field_2_top_col_gutter;
        rec.field_3_row_level_max = field_3_row_level_max;
        rec.field_4_col_level_max = field_4_col_level_max;
        return rec;
    }
}
