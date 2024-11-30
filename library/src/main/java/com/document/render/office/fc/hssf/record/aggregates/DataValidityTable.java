

package com.document.render.office.fc.hssf.record.aggregates;

import com.document.render.office.fc.hssf.model.RecordStream;
import com.document.render.office.fc.hssf.record.DVALRecord;
import com.document.render.office.fc.hssf.record.DVRecord;
import com.document.render.office.fc.hssf.record.Record;

import java.util.ArrayList;
import java.util.List;



public final class DataValidityTable extends RecordAggregate {

    private final DVALRecord _headerRec;

    private final List _validationList;

    public DataValidityTable(RecordStream rs) {
        _headerRec = (DVALRecord) rs.getNext();
        List temp = new ArrayList();
        while (rs.peekNextClass() == DVRecord.class) {
            temp.add(rs.getNext());
        }
        _validationList = temp;
    }

    public DataValidityTable() {
        _headerRec = new DVALRecord();
        _validationList = new ArrayList();
    }

    public void visitContainedRecords(RecordVisitor rv) {
        if (_validationList.isEmpty()) {
            return;
        }
        rv.visitRecord(_headerRec);
        for (int i = 0; i < _validationList.size(); i++) {
            rv.visitRecord((Record) _validationList.get(i));
        }
    }

    public void addDataValidation(DVRecord dvRecord) {
        _validationList.add(dvRecord);
        _headerRec.setDVRecNo(_validationList.size());
    }
}
