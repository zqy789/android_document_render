

package com.document.render.office.fc.hssf.record;

import com.document.render.office.fc.util.HexDump;


public final class LabelRecord extends Record implements CellValueRecordInterface {
    public final static short sid = 0x0204;

    private int field_1_row;
    private short field_2_column;
    private short field_3_xf_index;
    private short field_4_string_len;
    private byte field_5_unicode_flag;
    private String field_6_value;


    public LabelRecord() {
    }


    public LabelRecord(RecordInputStream in) {
        field_1_row = in.readUShort();
        field_2_column = in.readShort();
        field_3_xf_index = in.readShort();
        field_4_string_len = in.readShort();
        field_5_unicode_flag = in.readByte();
        if (field_4_string_len > 0) {
            if (isUnCompressedUnicode()) {
                field_6_value = in.readUnicodeLEString(field_4_string_len);
            } else {
                field_6_value = in.readCompressedUnicode(field_4_string_len);
            }
        } else {
            field_6_value = "";
        }
    }


    public int getRow() {
        return field_1_row;
    }


    public void setRow(int row) {
    }

    public short getColumn() {
        return field_2_column;
    }


    public void setColumn(short col) {
    }

    public short getXFIndex() {
        return field_3_xf_index;
    }


    public void setXFIndex(short xf) {
    }


    public short getStringLength() {
        return field_4_string_len;
    }


    public boolean isUnCompressedUnicode() {
        return (field_5_unicode_flag == 1);
    }


    public String getValue() {
        return field_6_value;
    }


    public int serialize(int offset, byte[] data) {
        throw new RecordFormatException("Label Records are supported READ ONLY...convert to LabelSST");
    }

    public int getRecordSize() {
        throw new RecordFormatException("Label Records are supported READ ONLY...convert to LabelSST");
    }

    public short getSid() {
        return sid;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[LABEL]\n");
        sb.append("    .row       = ").append(HexDump.shortToHex(getRow())).append("\n");
        sb.append("    .column    = ").append(HexDump.shortToHex(getColumn())).append("\n");
        sb.append("    .xfindex   = ").append(HexDump.shortToHex(getXFIndex())).append("\n");
        sb.append("    .string_len= ").append(HexDump.shortToHex(field_4_string_len)).append("\n");
        sb.append("    .unicode_flag= ").append(HexDump.byteToHex(field_5_unicode_flag)).append("\n");
        sb.append("    .value       = ").append(getValue()).append("\n");
        sb.append("[/LABEL]\n");
        return sb.toString();
    }

    public Object clone() {
        LabelRecord rec = new LabelRecord();
        rec.field_1_row = field_1_row;
        rec.field_2_column = field_2_column;
        rec.field_3_xf_index = field_3_xf_index;
        rec.field_4_string_len = field_4_string_len;
        rec.field_5_unicode_flag = field_5_unicode_flag;
        rec.field_6_value = field_6_value;
        return rec;
    }
}
