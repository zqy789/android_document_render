

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.StringUtil;


public final class NoteRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x001C;

    public static final NoteRecord[] EMPTY_ARRAY = {};


    public final static short NOTE_HIDDEN = 0x0;


    public final static short NOTE_VISIBLE = 0x2;

    private static final Byte DEFAULT_PADDING = Byte.valueOf((byte) 0);

    private int field_1_row;
    private int field_2_col;
    private short field_3_flags;
    private int field_4_shapeid;
    private boolean field_5_hasMultibyte;
    private String field_6_author;

    private Byte field_7_padding;


    public NoteRecord() {
        field_6_author = "";
        field_3_flags = 0;
        field_7_padding = DEFAULT_PADDING;
    }


    public NoteRecord(RecordInputStream in) {
        field_1_row = in.readUShort();
        field_2_col = in.readShort();
        field_3_flags = in.readShort();
        field_4_shapeid = in.readUShort();
        int length = in.readShort();
        field_5_hasMultibyte = in.readByte() != 0x00;
        if (field_5_hasMultibyte) {
            field_6_author = StringUtil.readUnicodeLE(in, length);
        } else {
            field_6_author = StringUtil.readCompressedUnicode(in, length);
        }
        if (in.available() == 1) {
            field_7_padding = Byte.valueOf(in.readByte());
        }
    }


    public short getSid() {
        return sid;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_row);
        out.writeShort(field_2_col);
        out.writeShort(field_3_flags);
        out.writeShort(field_4_shapeid);
        out.writeShort(field_6_author.length());
        out.writeByte(field_5_hasMultibyte ? 0x01 : 0x00);
        if (field_5_hasMultibyte) {
            StringUtil.putUnicodeLE(field_6_author, out);
        } else {
            StringUtil.putCompressedUnicode(field_6_author, out);
        }
        if (field_7_padding != null) {
            out.writeByte(field_7_padding.intValue());
        }
    }

    protected int getDataSize() {
        return 11
                + field_6_author.length() * (field_5_hasMultibyte ? 2 : 1)
                + (field_7_padding == null ? 0 : 1);
    }


    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[NOTE]\n");
        buffer.append("    .row    = ").append(field_1_row).append("\n");
        buffer.append("    .col    = ").append(field_2_col).append("\n");
        buffer.append("    .flags  = ").append(field_3_flags).append("\n");
        buffer.append("    .shapeid= ").append(field_4_shapeid).append("\n");
        buffer.append("    .author = ").append(field_6_author).append("\n");
        buffer.append("[/NOTE]\n");
        return buffer.toString();
    }


    public int getRow() {
        return field_1_row;
    }


    public void setRow(int row) {
        field_1_row = row;
    }


    public int getColumn() {
        return field_2_col;
    }


    public void setColumn(int col) {
        field_2_col = col;
    }


    public short getFlags() {
        return field_3_flags;
    }


    public void setFlags(short flags) {
        field_3_flags = flags;
    }


    protected boolean authorIsMultibyte() {
        return field_5_hasMultibyte;
    }


    public int getShapeId() {
        return field_4_shapeid;
    }


    public void setShapeId(int id) {
        field_4_shapeid = id;
    }


    public String getAuthor() {
        return field_6_author;
    }


    public void setAuthor(String author) {
        field_6_author = author;
        field_5_hasMultibyte = StringUtil.hasMultibyte(author);
    }

    public Object clone() {
        NoteRecord rec = new NoteRecord();
        rec.field_1_row = field_1_row;
        rec.field_2_col = field_2_col;
        rec.field_3_flags = field_3_flags;
        rec.field_4_shapeid = field_4_shapeid;
        rec.field_6_author = field_6_author;
        return rec;
    }
}
