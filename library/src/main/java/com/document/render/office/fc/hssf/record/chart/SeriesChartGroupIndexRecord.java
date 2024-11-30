

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class SeriesChartGroupIndexRecord extends StandardRecord {
    public final static short sid = 0x1045;
    private short field_1_chartGroupIndex;


    public SeriesChartGroupIndexRecord() {

    }

    public SeriesChartGroupIndexRecord(RecordInputStream in) {
        field_1_chartGroupIndex = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[SERTOCRT]\n");
        buffer.append("    .chartGroupIndex      = ")
                .append("0x").append(HexDump.toHex(getChartGroupIndex()))
                .append(" (").append(getChartGroupIndex()).append(" )");
        buffer.append(System.getProperty("line.separator"));

        buffer.append("[/SERTOCRT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_chartGroupIndex);
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        SeriesChartGroupIndexRecord rec = new SeriesChartGroupIndexRecord();

        rec.field_1_chartGroupIndex = field_1_chartGroupIndex;
        return rec;
    }



    public short getChartGroupIndex() {
        return field_1_chartGroupIndex;
    }


    public void setChartGroupIndex(short field_1_chartGroupIndex) {
        this.field_1_chartGroupIndex = field_1_chartGroupIndex;
    }
}
