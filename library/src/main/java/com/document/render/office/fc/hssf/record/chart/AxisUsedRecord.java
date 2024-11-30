

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class AxisUsedRecord extends StandardRecord {
    public final static short sid = 0x1046;
    private short field_1_numAxis;


    public AxisUsedRecord() {

    }

    public AxisUsedRecord(RecordInputStream in) {
        field_1_numAxis = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[AXISUSED]\n");
        buffer.append("    .numAxis              = ")
                .append("0x").append(HexDump.toHex(getNumAxis()))
                .append(" (").append(getNumAxis()).append(" )");
        buffer.append(System.getProperty("line.separator"));

        buffer.append("[/AXISUSED]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_numAxis);
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        AxisUsedRecord rec = new AxisUsedRecord();

        rec.field_1_numAxis = field_1_numAxis;
        return rec;
    }



    public short getNumAxis() {
        return field_1_numAxis;
    }


    public void setNumAxis(short field_1_numAxis) {
        this.field_1_numAxis = field_1_numAxis;
    }
}
