

package com.document.render.office.fc.hssf.record.cont;


import com.document.render.office.fc.hssf.record.ContinueRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.util.LittleEndianByteArrayOutputStream;
import com.document.render.office.fc.util.LittleEndianOutput;



public abstract class ContinuableRecord extends Record {

    protected ContinuableRecord() {

    }


    protected abstract void serialize(ContinuableRecordOutput out);



    public final int getRecordSize() {
        ContinuableRecordOutput out = ContinuableRecordOutput.createForCountingOnly();
        serialize(out);
        out.terminate();
        return out.getTotalSize();
    }

    public final int serialize(int offset, byte[] data) {

        LittleEndianOutput leo = new LittleEndianByteArrayOutputStream(data, offset);
        ContinuableRecordOutput out = new ContinuableRecordOutput(leo, getSid());
        serialize(out);
        out.terminate();
        return out.getTotalSize();
    }
}
