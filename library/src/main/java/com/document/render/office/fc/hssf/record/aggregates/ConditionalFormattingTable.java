

package com.document.render.office.fc.hssf.record.aggregates;

import com.document.render.office.fc.hssf.formula.FormulaShifter;
import com.document.render.office.fc.hssf.model.RecordStream;
import com.document.render.office.fc.hssf.record.CFHeaderRecord;

import java.util.ArrayList;
import java.util.List;



public final class ConditionalFormattingTable extends RecordAggregate {

    private final List _cfHeaders;


    public ConditionalFormattingTable() {
        _cfHeaders = new ArrayList();
    }

    public ConditionalFormattingTable(RecordStream rs) {

        List temp = new ArrayList();
        while (rs.peekNextClass() == CFHeaderRecord.class) {
            temp.add(CFRecordsAggregate.createCFAggregate(rs));
        }
        _cfHeaders = temp;
    }

    public void visitContainedRecords(RecordVisitor rv) {
        for (int i = 0; i < _cfHeaders.size(); i++) {
            CFRecordsAggregate subAgg = (CFRecordsAggregate) _cfHeaders.get(i);
            subAgg.visitContainedRecords(rv);
        }
    }


    public int add(CFRecordsAggregate cfAggregate) {
        _cfHeaders.add(cfAggregate);
        return _cfHeaders.size() - 1;
    }

    public int size() {
        return _cfHeaders.size();
    }

    public CFRecordsAggregate get(int index) {
        checkIndex(index);
        return (CFRecordsAggregate) _cfHeaders.get(index);
    }

    public void remove(int index) {
        checkIndex(index);
        _cfHeaders.remove(index);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= _cfHeaders.size()) {
            throw new IllegalArgumentException("Specified CF index " + index
                    + " is outside the allowable range (0.." + (_cfHeaders.size() - 1) + ")");
        }
    }

    public void updateFormulasAfterCellShift(FormulaShifter shifter, int externSheetIndex) {
        for (int i = 0; i < _cfHeaders.size(); i++) {
            CFRecordsAggregate subAgg = (CFRecordsAggregate) _cfHeaders.get(i);
            boolean shouldKeep = subAgg.updateFormulasAfterCellShift(shifter, externSheetIndex);
            if (!shouldKeep) {
                _cfHeaders.remove(i);
                i--;
            }
        }
    }
}
