

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.LittleEndianOutput;




public final class EndRecord extends StandardRecord {
    public static final short sid = 0x1034;

    public EndRecord() {
    }


    public EndRecord(RecordInputStream in) {
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[END]\n");
        buffer.append("[/END]\n");
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
        EndRecord er = new EndRecord();

        return er;
    }
}
