

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class PlotAreaRecord extends StandardRecord {
    public final static short sid = 0x1035;


    public PlotAreaRecord() {

    }


    public PlotAreaRecord(RecordInputStream in) {

    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[PLOTAREA]\n");

        buffer.append("[/PLOTAREA]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
    }

    protected int getDataSize() {
        return 0;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        PlotAreaRecord rec = new PlotAreaRecord();

        return rec;
    }
}
