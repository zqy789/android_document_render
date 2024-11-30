

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class AxisLineFormatRecord extends StandardRecord {
    public final static short sid = 0x1021;
    public final static short AXIS_TYPE_AXIS_LINE = 0;
    public final static short AXIS_TYPE_MAJOR_GRID_LINE = 1;
    public final static short AXIS_TYPE_MINOR_GRID_LINE = 2;
    public final static short AXIS_TYPE_WALLS_OR_FLOOR = 3;
    private short field_1_axisType;


    public AxisLineFormatRecord() {

    }

    public AxisLineFormatRecord(RecordInputStream in) {
        field_1_axisType = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[AXISLINEFORMAT]\n");
        buffer.append("    .axisType             = ")
                .append("0x").append(HexDump.toHex(getAxisType()))
                .append(" (").append(getAxisType()).append(" )");
        buffer.append(System.getProperty("line.separator"));

        buffer.append("[/AXISLINEFORMAT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_axisType);
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        AxisLineFormatRecord rec = new AxisLineFormatRecord();

        rec.field_1_axisType = field_1_axisType;
        return rec;
    }



    public short getAxisType() {
        return field_1_axisType;
    }


    public void setAxisType(short field_1_axisType) {
        this.field_1_axisType = field_1_axisType;
    }
}
