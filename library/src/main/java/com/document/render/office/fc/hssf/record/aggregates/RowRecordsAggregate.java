

package com.document.render.office.fc.hssf.record.aggregates;

import com.document.render.office.fc.hssf.formula.FormulaShifter;
import com.document.render.office.fc.hssf.model.RecordStream;
import com.document.render.office.fc.hssf.record.ArrayRecord;
import com.document.render.office.fc.hssf.record.CellValueRecordInterface;
import com.document.render.office.fc.hssf.record.ContinueRecord;
import com.document.render.office.fc.hssf.record.DBCellRecord;
import com.document.render.office.fc.hssf.record.DimensionsRecord;
import com.document.render.office.fc.hssf.record.FormulaRecord;
import com.document.render.office.fc.hssf.record.IndexRecord;
import com.document.render.office.fc.hssf.record.MergeCellsRecord;
import com.document.render.office.fc.hssf.record.MulBlankRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.RowRecord;
import com.document.render.office.fc.hssf.record.SharedFormulaRecord;
import com.document.render.office.fc.hssf.record.TableRecord;
import com.document.render.office.fc.hssf.record.UnknownRecord;
import com.document.render.office.fc.ss.SpreadsheetVersion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



public final class RowRecordsAggregate extends RecordAggregate {
    private final Map<Integer, RowRecord> _rowRecords;
    private final ValueRecordsAggregate _valuesAgg;
    private final List<Record> _unknownRecords;
    private final SharedValueManager _sharedValueManager;
    private int _firstrow = -1;
    private int _lastrow = -1;


    private RowRecord[] _rowRecordValues = null;


    public RowRecordsAggregate() {
        this(SharedValueManager.createEmpty());
    }

    private RowRecordsAggregate(SharedValueManager svm) {
        if (svm == null) {
            throw new IllegalArgumentException("SharedValueManager must be provided.");
        }
        _rowRecords = new TreeMap<Integer, RowRecord>();
        _valuesAgg = new ValueRecordsAggregate();
        _unknownRecords = new ArrayList<Record>();
        _sharedValueManager = svm;
    }


    public RowRecordsAggregate(RecordStream rs, SharedValueManager svm) {
        this(svm);
        while (rs.hasNext()) {
            Record rec = rs.getNext();
            switch (rec.getSid()) {
                case RowRecord.sid:
                    insertRow((RowRecord) rec);
                    continue;
                case DBCellRecord.sid:


                    continue;
            }
            if (rec instanceof UnknownRecord) {

                addUnknownRecord(rec);
                while (rs.peekNextSid() == ContinueRecord.sid) {
                    addUnknownRecord(rs.getNext());
                }
                continue;
            }
            if (rec instanceof MulBlankRecord) {
                _valuesAgg.addMultipleBlanks((MulBlankRecord) rec);
                continue;
            }
            if (!(rec instanceof CellValueRecordInterface)) {
                throw new RuntimeException("Unexpected record type (" + rec.getClass().getName() + ")");
            }
            _valuesAgg.construct((CellValueRecordInterface) rec, rs, svm);
        }
    }


    public static RowRecord createRow(int rowNumber) {
        return new RowRecord(rowNumber);
    }


    private void addUnknownRecord(Record rec) {







        _unknownRecords.add(rec);
    }

    public void insertRow(RowRecord row) {

        _rowRecords.put(Integer.valueOf(row.getRowNumber()), row);

        _rowRecordValues = null;
        if ((row.getRowNumber() < _firstrow) || (_firstrow == -1)) {
            _firstrow = row.getRowNumber();
        }
        if ((row.getRowNumber() > _lastrow) || (_lastrow == -1)) {
            _lastrow = row.getRowNumber();
        }
    }

    public void removeRow(RowRecord row) {
        int rowIndex = row.getRowNumber();
        _valuesAgg.removeAllCellsValuesForRow(rowIndex);
        Integer key = Integer.valueOf(rowIndex);
        RowRecord rr = _rowRecords.remove(key);
        if (rr == null) {
            throw new RuntimeException("Invalid row index (" + key.intValue() + ")");
        }
        if (row != rr) {
            _rowRecords.put(key, rr);
            throw new RuntimeException("Attempt to remove row that does not belong to this sheet");
        }


        _rowRecordValues = null;
    }

    public RowRecord getRow(int rowIndex) {
        int maxrow = SpreadsheetVersion.EXCEL97.getLastRowIndex();
        if (rowIndex < 0 || rowIndex > maxrow) {
            throw new IllegalArgumentException("The row number must be between 0 and " + maxrow);
        }
        return _rowRecords.get(Integer.valueOf(rowIndex));
    }

    public int getPhysicalNumberOfRows() {
        return _rowRecords.size();
    }

    public int getFirstRowNum() {
        return _firstrow;
    }

    public int getLastRowNum() {
        return _lastrow;
    }


    public int getRowBlockCount() {
        int size = _rowRecords.size() / DBCellRecord.BLOCK_SIZE;
        if ((_rowRecords.size() % DBCellRecord.BLOCK_SIZE) != 0)
            size++;
        return size;
    }

    private int getRowBlockSize(int block) {
        return RowRecord.ENCODED_SIZE * getRowCountForBlock(block);
    }


    public int getRowCountForBlock(int block) {
        int startIndex = block * DBCellRecord.BLOCK_SIZE;
        int endIndex = startIndex + DBCellRecord.BLOCK_SIZE - 1;
        if (endIndex >= _rowRecords.size())
            endIndex = _rowRecords.size() - 1;

        return endIndex - startIndex + 1;
    }


    private int getStartRowNumberForBlock(int block) {
        int startIndex = block * DBCellRecord.BLOCK_SIZE;

        if (_rowRecordValues == null) {
            _rowRecordValues = _rowRecords.values().toArray(new RowRecord[_rowRecords.size()]);
        }

        try {
            return _rowRecordValues[startIndex].getRowNumber();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Did not find start row for block " + block);
        }
    }


    private int getEndRowNumberForBlock(int block) {
        int endIndex = ((block + 1) * DBCellRecord.BLOCK_SIZE) - 1;
        if (endIndex >= _rowRecords.size())
            endIndex = _rowRecords.size() - 1;

        if (_rowRecordValues == null) {
            _rowRecordValues = _rowRecords.values().toArray(new RowRecord[_rowRecords.size()]);
        }

        try {
            return _rowRecordValues[endIndex].getRowNumber();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Did not find end row for block " + block);
        }
    }

    private int visitRowRecordsForBlock(int blockIndex, RecordVisitor rv) {
        final int startIndex = blockIndex * DBCellRecord.BLOCK_SIZE;
        final int endIndex = startIndex + DBCellRecord.BLOCK_SIZE;

        Iterator<RowRecord> rowIterator = _rowRecords.values().iterator();





        int i = 0;
        for (; i < startIndex; i++)
            rowIterator.next();
        int result = 0;
        while (rowIterator.hasNext() && (i++ < endIndex)) {
            Record rec = (Record) rowIterator.next();
            result += rec.getRecordSize();
            rv.visitRecord(rec);
        }
        return result;
    }

    public void visitContainedRecords(RecordVisitor rv) {

        PositionTrackingVisitor stv = new PositionTrackingVisitor(rv, 0);

        final int blockCount = getRowBlockCount();
        for (int blockIndex = 0; blockIndex < blockCount; blockIndex++) {


            int pos = 0;

            final int rowBlockSize = visitRowRecordsForBlock(blockIndex, rv);
            pos += rowBlockSize;

            final int startRowNumber = getStartRowNumberForBlock(blockIndex);
            final int endRowNumber = getEndRowNumberForBlock(blockIndex);
            DBCellRecord.Builder dbcrBuilder = new DBCellRecord.Builder();

            int cellRefOffset = (rowBlockSize - RowRecord.ENCODED_SIZE);
            for (int row = startRowNumber; row <= endRowNumber; row++) {
                if (_valuesAgg.rowHasCells(row)) {
                    stv.setPosition(0);
                    _valuesAgg.visitCellsForRow(row, stv);
                    int rowCellSize = stv.getPosition();
                    pos += rowCellSize;


                    dbcrBuilder.addCellOffset(cellRefOffset);
                    cellRefOffset = rowCellSize;
                }
            }

            rv.visitRecord(dbcrBuilder.build(pos));
        }
        for (int i = 0; i < _unknownRecords.size(); i++) {

            rv.visitRecord(_unknownRecords.get(i));
        }
    }

    public Iterator<RowRecord> getIterator() {
        return _rowRecords.values().iterator();
    }

    public int findStartOfRowOutlineGroup(int row) {

        RowRecord rowRecord = this.getRow(row);
        int level = rowRecord.getOutlineLevel();
        int currentRow = row;
        while (this.getRow(currentRow) != null) {
            rowRecord = this.getRow(currentRow);
            if (rowRecord.getOutlineLevel() < level) {
                return currentRow + 1;
            }
            currentRow--;
        }

        return currentRow + 1;
    }

    public int findEndOfRowOutlineGroup(int row) {
        int level = getRow(row).getOutlineLevel();
        int currentRow;
        for (currentRow = row; currentRow < getLastRowNum(); currentRow++) {
            if (getRow(currentRow) == null || getRow(currentRow).getOutlineLevel() < level) {
                break;
            }
        }

        return currentRow - 1;
    }


    private int writeHidden(RowRecord pRowRecord, int row) {
        int rowIx = row;
        RowRecord rowRecord = pRowRecord;
        int level = rowRecord.getOutlineLevel();
        while (rowRecord != null && getRow(rowIx).getOutlineLevel() >= level) {
            rowRecord.setZeroHeight(true);
            rowIx++;
            rowRecord = getRow(rowIx);
        }
        return rowIx;
    }

    public void collapseRow(int rowNumber) {


        int startRow = findStartOfRowOutlineGroup(rowNumber);
        RowRecord rowRecord = getRow(startRow);


        int nextRowIx = writeHidden(rowRecord, startRow);

        RowRecord row = getRow(nextRowIx);
        if (row == null) {
            row = createRow(nextRowIx);
            insertRow(row);
        }

        row.setColapsed(true);
    }

    public boolean isRowGroupCollapsed(int row) {
        int collapseRow = findEndOfRowOutlineGroup(row) + 1;

        if (getRow(collapseRow) == null) {
            return false;
        }
        return getRow(collapseRow).getColapsed();
    }

    public void expandRow(int rowNumber) {
        int idx = rowNumber;
        if (idx == -1)
            return;


        if (!isRowGroupCollapsed(idx)) {
            return;
        }


        int startIdx = findStartOfRowOutlineGroup(idx);
        RowRecord row = getRow(startIdx);


        int endIdx = findEndOfRowOutlineGroup(idx);








        if (!isRowGroupHiddenByParent(idx)) {
            for (int i = startIdx; i <= endIdx; i++) {
                RowRecord otherRow = getRow(i);
                if (row.getOutlineLevel() == otherRow.getOutlineLevel() || !isRowGroupCollapsed(i)) {
                    otherRow.setZeroHeight(false);
                }
            }
        }


        getRow(endIdx + 1).setColapsed(false);
    }

    public boolean isRowGroupHiddenByParent(int row) {

        int endLevel;
        boolean endHidden;
        int endOfOutlineGroupIdx = findEndOfRowOutlineGroup(row);
        if (getRow(endOfOutlineGroupIdx + 1) == null) {
            endLevel = 0;
            endHidden = false;
        } else {
            endLevel = getRow(endOfOutlineGroupIdx + 1).getOutlineLevel();
            endHidden = getRow(endOfOutlineGroupIdx + 1).getZeroHeight();
        }


        int startLevel;
        boolean startHidden;
        int startOfOutlineGroupIdx = findStartOfRowOutlineGroup(row);
        if (startOfOutlineGroupIdx - 1 < 0 || getRow(startOfOutlineGroupIdx - 1) == null) {
            startLevel = 0;
            startHidden = false;
        } else {
            startLevel = getRow(startOfOutlineGroupIdx - 1).getOutlineLevel();
            startHidden = getRow(startOfOutlineGroupIdx - 1).getZeroHeight();
        }

        if (endLevel > startLevel) {
            return endHidden;
        }

        return startHidden;
    }


    public Iterator<CellValueRecordInterface> getCellValueIterator() {
        return _valuesAgg.iterator();
    }


    public CellValueRecordInterface[] getValueRecords() {
        return _valuesAgg.getValueRecords();
    }

    public IndexRecord createIndexRecord(int indexRecordOffset, int sizeOfInitialSheetRecords) {
        IndexRecord result = new IndexRecord();
        result.setFirstRow(_firstrow);
        result.setLastRowAdd1(_lastrow + 1);







        int blockCount = getRowBlockCount();

        int indexRecSize = IndexRecord.getRecordSizeForBlockCount(blockCount);

        int currentOffset = indexRecordOffset + indexRecSize + sizeOfInitialSheetRecords;

        for (int block = 0; block < blockCount; block++) {




            currentOffset += getRowBlockSize(block);

            currentOffset += _valuesAgg.getRowCellBlockSize(
                    getStartRowNumberForBlock(block), getEndRowNumberForBlock(block));


            result.addDbcell(currentOffset);

            currentOffset += (8 + (getRowCountForBlock(block) * 2));
        }
        return result;
    }

    public void insertCell(CellValueRecordInterface cvRec) {
        _valuesAgg.insertCell(cvRec);
    }

    public void removeCell(CellValueRecordInterface cvRec) {
        if (cvRec instanceof FormulaRecordAggregate) {
            ((FormulaRecordAggregate) cvRec).notifyFormulaChanging();
        }
        _valuesAgg.removeCell(cvRec);
    }

    public FormulaRecordAggregate createFormula(int row, int col) {
        FormulaRecord fr = new FormulaRecord();
        fr.setRow(row);
        fr.setColumn((short) col);
        return new FormulaRecordAggregate(fr, null, _sharedValueManager);
    }

    public void updateFormulasAfterRowShift(FormulaShifter formulaShifter, int currentExternSheetIndex) {
        _valuesAgg.updateFormulasAfterRowShift(formulaShifter, currentExternSheetIndex);
    }

    public DimensionsRecord createDimensions() {
        DimensionsRecord result = new DimensionsRecord();
        result.setFirstRow(_firstrow);
        result.setLastRow(_lastrow);
        result.setFirstCol((short) _valuesAgg.getFirstCellNum());
        result.setLastCol((short) _valuesAgg.getLastCellNum());
        return result;
    }

    public void dispose() {
        _rowRecords.clear();

        _valuesAgg.dispose();

        _unknownRecords.clear();

        _rowRecordValues = null;

    }
}
