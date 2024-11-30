

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class LegendRecord extends StandardRecord {
    public final static short sid = 0x1015;
    public final static byte TYPE_BOTTOM = 0;
    public final static byte TYPE_CORNER = 1;
    public final static byte TYPE_TOP = 2;
    public final static byte TYPE_RIGHT = 3;
    public final static byte TYPE_LEFT = 4;
    public final static byte TYPE_UNDOCKED = 7;
    public final static byte SPACING_CLOSE = 0;
    public final static byte SPACING_MEDIUM = 1;
    public final static byte SPACING_OPEN = 2;
    private static final BitField autoPosition = BitFieldFactory.getInstance(0x01);
    private static final BitField autoSeries = BitFieldFactory.getInstance(0x02);
    private static final BitField autoXPositioning = BitFieldFactory.getInstance(0x04);
    private static final BitField autoYPositioning = BitFieldFactory.getInstance(0x08);
    private static final BitField vertical = BitFieldFactory.getInstance(0x10);
    private static final BitField dataTable = BitFieldFactory.getInstance(0x20);
    private int field_1_xAxisUpperLeft;
    private int field_2_yAxisUpperLeft;
    private int field_3_xSize;
    private int field_4_ySize;
    private byte field_5_type;
    private byte field_6_spacing;
    private short field_7_options;


    public LegendRecord() {

    }

    public LegendRecord(RecordInputStream in) {
        field_1_xAxisUpperLeft = in.readInt();
        field_2_yAxisUpperLeft = in.readInt();
        field_3_xSize = in.readInt();
        field_4_ySize = in.readInt();
        field_5_type = in.readByte();
        field_6_spacing = in.readByte();
        field_7_options = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[LEGEND]\n");
        buffer.append("    .xAxisUpperLeft       = ")
                .append("0x").append(HexDump.toHex(getXAxisUpperLeft()))
                .append(" (").append(getXAxisUpperLeft()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .yAxisUpperLeft       = ")
                .append("0x").append(HexDump.toHex(getYAxisUpperLeft()))
                .append(" (").append(getYAxisUpperLeft()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .xSize                = ")
                .append("0x").append(HexDump.toHex(getXSize()))
                .append(" (").append(getXSize()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .ySize                = ")
                .append("0x").append(HexDump.toHex(getYSize()))
                .append(" (").append(getYSize()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .type                 = ")
                .append("0x").append(HexDump.toHex(getType()))
                .append(" (").append(getType()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .spacing              = ")
                .append("0x").append(HexDump.toHex(getSpacing()))
                .append(" (").append(getSpacing()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .options              = ")
                .append("0x").append(HexDump.toHex(getOptions()))
                .append(" (").append(getOptions()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .autoPosition             = ").append(isAutoPosition()).append('\n');
        buffer.append("         .autoSeries               = ").append(isAutoSeries()).append('\n');
        buffer.append("         .autoXPositioning         = ").append(isAutoXPositioning()).append('\n');
        buffer.append("         .autoYPositioning         = ").append(isAutoYPositioning()).append('\n');
        buffer.append("         .vertical                 = ").append(isVertical()).append('\n');
        buffer.append("         .dataTable                = ").append(isDataTable()).append('\n');

        buffer.append("[/LEGEND]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeInt(field_1_xAxisUpperLeft);
        out.writeInt(field_2_yAxisUpperLeft);
        out.writeInt(field_3_xSize);
        out.writeInt(field_4_ySize);
        out.writeByte(field_5_type);
        out.writeByte(field_6_spacing);
        out.writeShort(field_7_options);
    }

    protected int getDataSize() {
        return 4 + 4 + 4 + 4 + 1 + 1 + 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        LegendRecord rec = new LegendRecord();

        rec.field_1_xAxisUpperLeft = field_1_xAxisUpperLeft;
        rec.field_2_yAxisUpperLeft = field_2_yAxisUpperLeft;
        rec.field_3_xSize = field_3_xSize;
        rec.field_4_ySize = field_4_ySize;
        rec.field_5_type = field_5_type;
        rec.field_6_spacing = field_6_spacing;
        rec.field_7_options = field_7_options;
        return rec;
    }



    public int getXAxisUpperLeft() {
        return field_1_xAxisUpperLeft;
    }


    public void setXAxisUpperLeft(int field_1_xAxisUpperLeft) {
        this.field_1_xAxisUpperLeft = field_1_xAxisUpperLeft;
    }


    public int getYAxisUpperLeft() {
        return field_2_yAxisUpperLeft;
    }


    public void setYAxisUpperLeft(int field_2_yAxisUpperLeft) {
        this.field_2_yAxisUpperLeft = field_2_yAxisUpperLeft;
    }


    public int getXSize() {
        return field_3_xSize;
    }


    public void setXSize(int field_3_xSize) {
        this.field_3_xSize = field_3_xSize;
    }


    public int getYSize() {
        return field_4_ySize;
    }


    public void setYSize(int field_4_ySize) {
        this.field_4_ySize = field_4_ySize;
    }


    public byte getType() {
        return field_5_type;
    }


    public void setType(byte field_5_type) {
        this.field_5_type = field_5_type;
    }


    public byte getSpacing() {
        return field_6_spacing;
    }


    public void setSpacing(byte field_6_spacing) {
        this.field_6_spacing = field_6_spacing;
    }


    public short getOptions() {
        return field_7_options;
    }


    public void setOptions(short field_7_options) {
        this.field_7_options = field_7_options;
    }


    public boolean isAutoPosition() {
        return autoPosition.isSet(field_7_options);
    }


    public void setAutoPosition(boolean value) {
        field_7_options = autoPosition.setShortBoolean(field_7_options, value);
    }


    public boolean isAutoSeries() {
        return autoSeries.isSet(field_7_options);
    }


    public void setAutoSeries(boolean value) {
        field_7_options = autoSeries.setShortBoolean(field_7_options, value);
    }


    public boolean isAutoXPositioning() {
        return autoXPositioning.isSet(field_7_options);
    }


    public void setAutoXPositioning(boolean value) {
        field_7_options = autoXPositioning.setShortBoolean(field_7_options, value);
    }


    public boolean isAutoYPositioning() {
        return autoYPositioning.isSet(field_7_options);
    }


    public void setAutoYPositioning(boolean value) {
        field_7_options = autoYPositioning.setShortBoolean(field_7_options, value);
    }


    public boolean isVertical() {
        return vertical.isSet(field_7_options);
    }


    public void setVertical(boolean value) {
        field_7_options = vertical.setShortBoolean(field_7_options, value);
    }


    public boolean isDataTable() {
        return dataTable.isSet(field_7_options);
    }


    public void setDataTable(boolean value) {
        field_7_options = dataTable.setShortBoolean(field_7_options, value);
    }
}
