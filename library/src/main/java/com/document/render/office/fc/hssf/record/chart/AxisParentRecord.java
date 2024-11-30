

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.hssf.record.UnknownRecord;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class AxisParentRecord extends StandardRecord {
    public final static short sid = 0x1041;
    public final static short AXIS_TYPE_MAIN = 0;
    public final static short AXIS_TYPE_SECONDARY = 1;
    private short field_1_axisType;
    private int field_2_x;
    private int field_3_y;
    private int field_4_width;
    private int field_5_height;


    public AxisParentRecord() {

    }

    public AxisParentRecord(RecordInputStream in) {
        field_1_axisType = in.readShort();
        field_2_x = in.readInt();
        field_3_y = in.readInt();
        field_4_width = in.readInt();
        field_5_height = in.readInt();
    }

    public AxisParentRecord(UnknownRecord unknownRecord) {
        if (unknownRecord.getSid() == AxisParentRecord.sid && unknownRecord.getData().length == getDataSize()) {
            byte[] data = unknownRecord.getData();

            field_1_axisType = LittleEndian.getShort(data, 0);
            field_2_x = LittleEndian.getInt(data, 2);
            field_3_y = LittleEndian.getInt(data, 6);
            field_4_width = LittleEndian.getInt(data, 10);
            field_5_height = LittleEndian.getInt(data, 14);

        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[AXISPARENT]\n");
        buffer.append("    .axisType             = ")
                .append("0x").append(HexDump.toHex(getAxisType()))
                .append(" (").append(getAxisType()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .x                    = ")
                .append("0x").append(HexDump.toHex(getX()))
                .append(" (").append(getX()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .y                    = ")
                .append("0x").append(HexDump.toHex(getY()))
                .append(" (").append(getY()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .width                = ")
                .append("0x").append(HexDump.toHex(getWidth()))
                .append(" (").append(getWidth()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .height               = ")
                .append("0x").append(HexDump.toHex(getHeight()))
                .append(" (").append(getHeight()).append(" )");
        buffer.append(System.getProperty("line.separator"));

        buffer.append("[/AXISPARENT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_axisType);
        out.writeInt(field_2_x);
        out.writeInt(field_3_y);
        out.writeInt(field_4_width);
        out.writeInt(field_5_height);
    }

    protected int getDataSize() {
        return 2 + 4 + 4 + 4 + 4;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        AxisParentRecord rec = new AxisParentRecord();

        rec.field_1_axisType = field_1_axisType;
        rec.field_2_x = field_2_x;
        rec.field_3_y = field_3_y;
        rec.field_4_width = field_4_width;
        rec.field_5_height = field_5_height;
        return rec;
    }



    public short getAxisType() {
        return field_1_axisType;
    }


    public void setAxisType(short field_1_axisType) {
        this.field_1_axisType = field_1_axisType;
    }


    public int getX() {
        return field_2_x;
    }


    public void setX(int field_2_x) {
        this.field_2_x = field_2_x;
    }


    public int getY() {
        return field_3_y;
    }


    public void setY(int field_3_y) {
        this.field_3_y = field_3_y;
    }


    public int getWidth() {
        return field_4_width;
    }


    public void setWidth(int field_4_width) {
        this.field_4_width = field_4_width;
    }


    public int getHeight() {
        return field_5_height;
    }


    public void setHeight(int field_5_height) {
        this.field_5_height = field_5_height;
    }
}
