

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class PlotGrowthRecord extends StandardRecord {
    public final static short sid = 0x1064;
    private int field_1_horizontalScale;
    private int field_2_verticalScale;


    public PlotGrowthRecord() {

    }

    public PlotGrowthRecord(RecordInputStream in) {
        field_1_horizontalScale = in.readInt();
        field_2_verticalScale = in.readInt();

    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[PLOTGROWTH]\n");
        buffer.append("    .horizontalScale      = ")
                .append("0x").append(HexDump.toHex(getHorizontalScale()))
                .append(" (").append(getHorizontalScale()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .verticalScale        = ")
                .append("0x").append(HexDump.toHex(getVerticalScale()))
                .append(" (").append(getVerticalScale()).append(" )");
        buffer.append(System.getProperty("line.separator"));

        buffer.append("[/PLOTGROWTH]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeInt(field_1_horizontalScale);
        out.writeInt(field_2_verticalScale);
    }

    protected int getDataSize() {
        return 4 + 4;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        PlotGrowthRecord rec = new PlotGrowthRecord();

        rec.field_1_horizontalScale = field_1_horizontalScale;
        rec.field_2_verticalScale = field_2_verticalScale;
        return rec;
    }



    public int getHorizontalScale() {
        return field_1_horizontalScale;
    }


    public void setHorizontalScale(int field_1_horizontalScale) {
        this.field_1_horizontalScale = field_1_horizontalScale;
    }


    public int getVerticalScale() {
        return field_2_verticalScale;
    }


    public void setVerticalScale(int field_2_verticalScale) {
        this.field_2_verticalScale = field_2_verticalScale;
    }
}
