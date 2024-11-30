

package com.document.render.office.fc.hssf.eventmodel;

import com.document.render.office.fc.hssf.record.Record;


public interface ERFListener {

    public boolean processRecord(Record rec);
}
