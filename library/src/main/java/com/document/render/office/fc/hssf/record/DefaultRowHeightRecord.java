


package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;



public final class DefaultRowHeightRecord
        extends StandardRecord {
    @Keep
    public final static short sid = 0x225;

    public static final short DEFAULT_ROW_HEIGHT = 0xFF;
    private short field_1_option_flags;
    private short field_2_row_height;

    public DefaultRowHeightRecord() {
        field_1_option_flags = 0x0000;
        field_2_row_height = DEFAULT_ROW_HEIGHT;
    }

    public DefaultRowHeightRecord(RecordInputStream in) {
        field_1_option_flags = in.readShort();
        field_2_row_height = in.readShort();
    }



    public short getOptionFlags() {
        return field_1_option_flags;
    }



    public void setOptionFlags(short flags) {
        field_1_option_flags = flags;
    }



    public short getRowHeight() {
        return field_2_row_height;
    }



    public void setRowHeight(short height) {
        field_2_row_height = height;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[DEFAULTROWHEIGHT]\n");
        buffer.append("    .optionflags    = ")
                .append(Integer.toHexString(getOptionFlags())).append("\n");
        buffer.append("    .rowheight      = ")
                .append(Integer.toHexString(getRowHeight())).append("\n");
        buffer.append("[/DEFAULTROWHEIGHT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getOptionFlags());
        out.writeShort(getRowHeight());
    }

    protected int getDataSize() {
        return 4;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        DefaultRowHeightRecord rec = new DefaultRowHeightRecord();
        rec.field_1_option_flags = field_1_option_flags;
        rec.field_2_row_height = field_2_row_height;
        return rec;
    }
}
