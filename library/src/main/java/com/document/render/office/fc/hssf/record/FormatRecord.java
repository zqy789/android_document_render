

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.StringUtil;


public final class FormatRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x041E;

    private final int field_1_index_code;
    private final boolean field_3_hasMultibyte;
    private final String field_4_formatstring;

    public FormatRecord(int indexCode, String fs) {
        field_1_index_code = indexCode;
        field_4_formatstring = fs;
        field_3_hasMultibyte = StringUtil.hasMultibyte(fs);
    }

    public FormatRecord(RecordInputStream in) {
        field_1_index_code = in.readShort();
        int field_3_unicode_len = in.readUShort();
        field_3_hasMultibyte = (in.readByte() & 0x01) != 0;

        if (field_3_hasMultibyte) {
            field_4_formatstring = in.readUnicodeLEString(field_3_unicode_len);
        } else {
            field_4_formatstring = in.readCompressedUnicode(field_3_unicode_len);
        }
    }


    public int getIndexCode() {
        return field_1_index_code;
    }


    public String getFormatString() {
        return field_4_formatstring;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[FORMAT]\n");
        buffer.append("    .indexcode       = ").append(HexDump.shortToHex(getIndexCode())).append("\n");
        buffer.append("    .isUnicode       = ").append(field_3_hasMultibyte).append("\n");
        buffer.append("    .formatstring    = ").append(getFormatString()).append("\n");
        buffer.append("[/FORMAT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        String formatString = getFormatString();
        out.writeShort(getIndexCode());
        out.writeShort(formatString.length());
        out.writeByte(field_3_hasMultibyte ? 0x01 : 0x00);

        if (field_3_hasMultibyte) {
            StringUtil.putUnicodeLE(formatString, out);
        } else {
            StringUtil.putCompressedUnicode(formatString, out);
        }
    }

    protected int getDataSize() {
        return 5
                + getFormatString().length() * (field_3_hasMultibyte ? 2 : 1);
    }

    public short getSid() {
        return sid;
    }

    @Override
    public Object clone() {

        return this;
    }
}
