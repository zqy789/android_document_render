

package com.document.render.office.fc.hssf.record;


public abstract class RecordBase {

    public abstract int serialize(int offset, byte[] data);


    public abstract int getRecordSize();
}
