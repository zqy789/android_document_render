

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import java.io.ByteArrayInputStream;



public final class DrawingRecordForBiffViewer extends AbstractEscherHolderRecord {
    @Keep
    public static final short sid = 0xEC;

    public DrawingRecordForBiffViewer() {
    }

    public DrawingRecordForBiffViewer(RecordInputStream in) {
        super(in);
    }

    public DrawingRecordForBiffViewer(DrawingRecord r) {
        super(convertToInputStream(r));
        convertRawBytesToEscherRecords();
    }

    private static RecordInputStream convertToInputStream(DrawingRecord r) {
        byte[] data = r.serialize();
        RecordInputStream rinp = new RecordInputStream(
                new ByteArrayInputStream(data)
        );
        rinp.nextRecord();
        return rinp;
    }

    protected String getRecordName() {
        return "MSODRAWING";
    }

    public short getSid() {
        return sid;
    }
}
