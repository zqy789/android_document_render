

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.StringUtil;



public final class SeriesTextRecord extends StandardRecord {
    public final static short sid = 0x100D;


    private static final int MAX_LEN = 0xFF;
    private int field_1_id;
    private boolean is16bit;
    private String field_4_text;

    public SeriesTextRecord() {
        field_4_text = "";
        is16bit = false;
    }

    public SeriesTextRecord(RecordInputStream in) {
        field_1_id = in.readUShort();
        int field_2_textLength = in.readUByte();
        is16bit = (in.readUByte() & 0x01) != 0;
        if (is16bit) {
            field_4_text = in.readUnicodeLEString(field_2_textLength);
        } else {
            field_4_text = in.readCompressedUnicode(field_2_textLength);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("[SERIESTEXT]\n");
        sb.append("  .id     =").append(HexDump.shortToHex(getId())).append('\n');
        sb.append("  .textLen=").append(field_4_text.length()).append('\n');
        sb.append("  .is16bit=").append(is16bit).append('\n');
        sb.append("  .text   =").append(" (").append(getText()).append(" )").append('\n');
        sb.append("[/SERIESTEXT]\n");
        return sb.toString();
    }

    public void serialize(LittleEndianOutput out) {

        out.writeShort(field_1_id);
        out.writeByte(field_4_text.length());
        if (is16bit) {

            out.writeByte(0x01);
            StringUtil.putUnicodeLE(field_4_text, out);
        } else {

            out.writeByte(0x00);
            StringUtil.putCompressedUnicode(field_4_text, out);
        }
    }

    protected int getDataSize() {
        return 2 + 1 + 1 + field_4_text.length() * (is16bit ? 2 : 1);
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        SeriesTextRecord rec = new SeriesTextRecord();

        rec.field_1_id = field_1_id;
        rec.is16bit = is16bit;
        rec.field_4_text = field_4_text;
        return rec;
    }


    public int getId() {
        return field_1_id;
    }


    public void setId(int id) {
        field_1_id = id;
    }


    public String getText() {
        return field_4_text;
    }


    public void setText(String text) {
        if (text.length() > MAX_LEN) {
            throw new IllegalArgumentException("Text is too long ("
                    + text.length() + ">" + MAX_LEN + ")");
        }
        field_4_text = text;
        is16bit = StringUtil.hasMultibyte(text);
    }
}
