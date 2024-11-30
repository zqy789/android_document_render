

package com.document.render.office.fc.hssf.eventusermodel.dummyrecord;

import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.RecordFormatException;


abstract class DummyRecordBase extends Record {

    protected DummyRecordBase() {

    }

    public final short getSid() {
        return -1;
    }

    public int serialize(int offset, byte[] data) {
        throw new RecordFormatException("Cannot serialize a dummy record");
    }

    public final int getRecordSize() {
        throw new RecordFormatException("Cannot serialize a dummy record");
    }
}
