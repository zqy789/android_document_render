

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class AreaFormatRecord extends StandardRecord {
    public final static short sid = 0x100A;

    private static final BitField automatic = BitFieldFactory.getInstance(0x1);
    private static final BitField invert = BitFieldFactory.getInstance(0x2);

    private int field_1_foregroundColor;
    private int field_2_backgroundColor;
    private short field_3_pattern;
    private short field_4_formatFlags;
    private short field_5_forecolorIndex;
    private short field_6_backcolorIndex;


    public AreaFormatRecord() {

    }

    public AreaFormatRecord(RecordInputStream in) {
        field_1_foregroundColor = in.readInt();
        field_2_backgroundColor = in.readInt();
        field_3_pattern = in.readShort();
        field_4_formatFlags = in.readShort();
        field_5_forecolorIndex = in.readShort();
        field_6_backcolorIndex = in.readShort();

    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[AREAFORMAT]\n");
        buffer.append("    .foregroundColor      = ")
                .append("0x").append(HexDump.toHex(getForegroundColor()))
                .append(" (").append(getForegroundColor()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .backgroundColor      = ")
                .append("0x").append(HexDump.toHex(getBackgroundColor()))
                .append(" (").append(getBackgroundColor()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .pattern              = ")
                .append("0x").append(HexDump.toHex(getPattern()))
                .append(" (").append(getPattern()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .formatFlags          = ")
                .append("0x").append(HexDump.toHex(getFormatFlags()))
                .append(" (").append(getFormatFlags()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .automatic                = ").append(isAutomatic()).append('\n');
        buffer.append("         .invert                   = ").append(isInvert()).append('\n');
        buffer.append("    .forecolorIndex       = ")
                .append("0x").append(HexDump.toHex(getForecolorIndex()))
                .append(" (").append(getForecolorIndex()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .backcolorIndex       = ")
                .append("0x").append(HexDump.toHex(getBackcolorIndex()))
                .append(" (").append(getBackcolorIndex()).append(" )");
        buffer.append(System.getProperty("line.separator"));

        buffer.append("[/AREAFORMAT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeInt(field_1_foregroundColor);
        out.writeInt(field_2_backgroundColor);
        out.writeShort(field_3_pattern);
        out.writeShort(field_4_formatFlags);
        out.writeShort(field_5_forecolorIndex);
        out.writeShort(field_6_backcolorIndex);
    }

    protected int getDataSize() {
        return 4 + 4 + 2 + 2 + 2 + 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        AreaFormatRecord rec = new AreaFormatRecord();

        rec.field_1_foregroundColor = field_1_foregroundColor;
        rec.field_2_backgroundColor = field_2_backgroundColor;
        rec.field_3_pattern = field_3_pattern;
        rec.field_4_formatFlags = field_4_formatFlags;
        rec.field_5_forecolorIndex = field_5_forecolorIndex;
        rec.field_6_backcolorIndex = field_6_backcolorIndex;
        return rec;
    }



    public int getForegroundColor() {
        return field_1_foregroundColor;
    }


    public void setForegroundColor(int field_1_foregroundColor) {
        this.field_1_foregroundColor = field_1_foregroundColor;
    }


    public int getBackgroundColor() {
        return field_2_backgroundColor;
    }


    public void setBackgroundColor(int field_2_backgroundColor) {
        this.field_2_backgroundColor = field_2_backgroundColor;
    }


    public short getPattern() {
        return field_3_pattern;
    }


    public void setPattern(short field_3_pattern) {
        this.field_3_pattern = field_3_pattern;
    }


    public short getFormatFlags() {
        return field_4_formatFlags;
    }


    public void setFormatFlags(short field_4_formatFlags) {
        this.field_4_formatFlags = field_4_formatFlags;
    }


    public short getForecolorIndex() {
        return field_5_forecolorIndex;
    }


    public void setForecolorIndex(short field_5_forecolorIndex) {
        this.field_5_forecolorIndex = field_5_forecolorIndex;
    }


    public short getBackcolorIndex() {
        return field_6_backcolorIndex;
    }


    public void setBackcolorIndex(short field_6_backcolorIndex) {
        this.field_6_backcolorIndex = field_6_backcolorIndex;
    }


    public boolean isAutomatic() {
        return automatic.isSet(field_4_formatFlags);
    }


    public void setAutomatic(boolean value) {
        field_4_formatFlags = automatic.setShortBoolean(field_4_formatFlags, value);
    }


    public boolean isInvert() {
        return invert.isSet(field_4_formatFlags);
    }


    public void setInvert(boolean value) {
        field_4_formatFlags = invert.setShortBoolean(field_4_formatFlags, value);
    }
}
