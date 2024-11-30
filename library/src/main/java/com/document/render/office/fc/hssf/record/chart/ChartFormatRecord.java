

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class ChartFormatRecord extends StandardRecord {
    public static final short sid = 0x1014;

    private static final BitField varyDisplayPattern = BitFieldFactory.getInstance(0x01);


    private int field1_x_position;
    private int field2_y_position;
    private int field3_width;
    private int field4_height;
    private int field5_grbit;
    private int field6_unknown;

    public ChartFormatRecord() {

    }

    public ChartFormatRecord(RecordInputStream in) {
        field1_x_position = in.readInt();
        field2_y_position = in.readInt();
        field3_width = in.readInt();
        field4_height = in.readInt();
        field5_grbit = in.readUShort();
        field6_unknown = in.readUShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[CHARTFORMAT]\n");
        buffer.append("    .xPosition       = ").append(getXPosition()).append("\n");
        buffer.append("    .yPosition       = ").append(getYPosition()).append("\n");
        buffer.append("    .width           = ").append(getWidth()).append("\n");
        buffer.append("    .height          = ").append(getHeight()).append("\n");
        buffer.append("    .grBit           = ").append(HexDump.intToHex(field5_grbit)).append("\n");
        buffer.append("[/CHARTFORMAT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeInt(getXPosition());
        out.writeInt(getYPosition());
        out.writeInt(getWidth());
        out.writeInt(getHeight());
        out.writeShort(field5_grbit);
        out.writeShort(field6_unknown);
    }

    protected int getDataSize() {
        return 20;
    }

    public short getSid() {
        return sid;
    }

    public int getXPosition() {
        return field1_x_position;
    }

    public void setXPosition(int xPosition) {
        field1_x_position = xPosition;
    }

    public int getYPosition() {
        return field2_y_position;
    }

    public void setYPosition(int yPosition) {
        field2_y_position = yPosition;
    }

    public int getWidth() {
        return field3_width;
    }

    public void setWidth(int width) {
        field3_width = width;
    }

    public int getHeight() {
        return field4_height;
    }

    public void setHeight(int height) {
        field4_height = height;
    }

    public boolean getVaryDisplayPattern() {
        return varyDisplayPattern.isSet(field5_grbit);
    }

    public void setVaryDisplayPattern(boolean value) {
        field5_grbit = varyDisplayPattern.setBoolean(field5_grbit, value);
    }
}
