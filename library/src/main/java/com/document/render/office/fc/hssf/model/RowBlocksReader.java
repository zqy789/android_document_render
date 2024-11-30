

package com.document.render.office.fc.hssf.model;

import com.document.render.office.fc.hssf.record.ArrayRecord;
import com.document.render.office.fc.hssf.record.FormulaRecord;
import com.document.render.office.fc.hssf.record.MergeCellsRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.SharedFormulaRecord;
import com.document.render.office.fc.hssf.record.TableRecord;
import com.document.render.office.fc.hssf.record.aggregates.MergedCellsTable;
import com.document.render.office.fc.hssf.record.aggregates.SharedValueManager;
import com.document.render.office.fc.ss.util.CellReference;

import java.util.ArrayList;
import java.util.List;



public final class RowBlocksReader {

    private final List _plainRecords;
    private final SharedValueManager _sfm;
    private final MergeCellsRecord[] _mergedCellsRecords;


    public RowBlocksReader(RecordStream rs) {
        List<Record> plainRecords = new ArrayList<Record>();
        List<Record> shFrmRecords = new ArrayList<Record>();
        List<CellReference> firstCellRefs = new ArrayList<CellReference>();
        List<Record> arrayRecords = new ArrayList<Record>();
        List<Record> tableRecords = new ArrayList<Record>();
        List<Record> mergeCellRecords = new ArrayList<Record>();

        Record prevRec = null;
        while (!RecordOrderer.isEndOfRowBlock(rs.peekNextSid())) {




            if (!rs.hasNext()) {
                throw new RuntimeException("Failed to find end of row/cell records");

            }
            Record rec = rs.getNext();
            List<Record> dest;
            switch (rec.getSid()) {
                case MergeCellsRecord.sid:
                    dest = mergeCellRecords;
                    break;
                case SharedFormulaRecord.sid:
                    dest = shFrmRecords;
                    if (!(prevRec instanceof FormulaRecord)) {
                        throw new RuntimeException("Shared formula record should follow a FormulaRecord");
                    }
                    FormulaRecord fr = (FormulaRecord) prevRec;
                    firstCellRefs.add(new CellReference(fr.getRow(), fr.getColumn()));
                    break;
                case ArrayRecord.sid:
                    dest = arrayRecords;
                    break;
                case TableRecord.sid:
                    dest = tableRecords;
                    break;
                default:
                    dest = plainRecords;
            }
            dest.add(rec);
            prevRec = rec;
        }
        SharedFormulaRecord[] sharedFormulaRecs = new SharedFormulaRecord[shFrmRecords.size()];
        CellReference[] firstCells = new CellReference[firstCellRefs.size()];
        ArrayRecord[] arrayRecs = new ArrayRecord[arrayRecords.size()];
        TableRecord[] tableRecs = new TableRecord[tableRecords.size()];
        shFrmRecords.toArray(sharedFormulaRecs);
        firstCellRefs.toArray(firstCells);
        arrayRecords.toArray(arrayRecs);
        tableRecords.toArray(tableRecs);

        _plainRecords = plainRecords;
        _sfm = SharedValueManager.create(sharedFormulaRecs, firstCells, arrayRecs, tableRecs);
        _mergedCellsRecords = new MergeCellsRecord[mergeCellRecords.size()];
        mergeCellRecords.toArray(_mergedCellsRecords);
    }


    public MergeCellsRecord[] getLooseMergedCells() {
        return _mergedCellsRecords;
    }

    public SharedValueManager getSharedFormulaManager() {
        return _sfm;
    }


    public RecordStream getPlainRecordStream() {
        return new RecordStream(_plainRecords, 0);
    }
}
