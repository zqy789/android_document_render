

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class SeriesListRecord extends StandardRecord {
    public final static short sid = 0x1016;
    private short[] field_1_seriesNumbers;

    public SeriesListRecord(short[] seriesNumbers) {
        field_1_seriesNumbers = seriesNumbers;
    }

    public SeriesListRecord(RecordInputStream in) {
        int nItems = in.readUShort();
        short[] ss = new short[nItems];
        for (int i = 0; i < nItems; i++) {
            ss[i] = in.readShort();

        }
        field_1_seriesNumbers = ss;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[SERIESLIST]\n");
        buffer.append("    .seriesNumbers= ").append(" (").append(getSeriesNumbers()).append(" )");
        buffer.append("\n");

        buffer.append("[/SERIESLIST]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {

        int nItems = field_1_seriesNumbers.length;
        out.writeShort(nItems);
        for (int i = 0; i < nItems; i++) {
            out.writeShort(field_1_seriesNumbers[i]);
        }
    }

    protected int getDataSize() {
        return field_1_seriesNumbers.length * 2 + 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        return new SeriesListRecord(field_1_seriesNumbers.clone());
    }


    public short[] getSeriesNumbers() {
        return field_1_seriesNumbers;
    }
}
