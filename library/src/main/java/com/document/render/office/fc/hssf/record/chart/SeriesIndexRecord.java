

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class SeriesIndexRecord extends StandardRecord {
    public final static short sid = 0x1065;
    private short field_1_index;


    public SeriesIndexRecord() {

    }

    public SeriesIndexRecord(RecordInputStream in) {
        field_1_index = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[SINDEX]\n");
        buffer.append("    .index                = ")
                .append("0x").append(HexDump.toHex(getIndex()))
                .append(" (").append(getIndex()).append(" )");
        buffer.append(System.getProperty("line.separator"));

        buffer.append("[/SINDEX]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_index);
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        SeriesIndexRecord rec = new SeriesIndexRecord();

        rec.field_1_index = field_1_index;
        return rec;
    }


    
    public short getIndex() {
        return field_1_index;
    }

    
    public void setIndex(short field_1_index) {
        this.field_1_index = field_1_index;
    }
}
