

package com.document.render.office.fc.hslf.record;


public interface ParentAwareRecord {
    public RecordContainer getParentRecord();

    public void setParentRecord(RecordContainer parentRecord);
}
