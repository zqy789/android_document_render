

package com.document.render.office.fc.hssf.record.aggregates;

import androidx.annotation.Keep;

import com.document.render.office.fc.hssf.formula.ptg.ExpPtg;
import com.document.render.office.fc.hssf.record.ArrayRecord;
import com.document.render.office.fc.hssf.record.FormulaRecord;
import com.document.render.office.fc.hssf.record.SharedFormulaRecord;
import com.document.render.office.fc.hssf.record.SharedValueRecordBase;
import com.document.render.office.fc.hssf.record.TableRecord;
import com.document.render.office.fc.hssf.util.CellRangeAddress8Bit;
import com.document.render.office.fc.ss.util.CellReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public final class SharedValueManager {

    private final List<ArrayRecord> _arrayRecords;
    private final TableRecord[] _tableRecords;
    private final Map<SharedFormulaRecord, SharedFormulaGroup> _groupsBySharedFormulaRecord;

    private Map<Integer, SharedFormulaGroup> _groupsCache;
    private SharedValueManager(SharedFormulaRecord[] sharedFormulaRecords,
                               CellReference[] firstCells, ArrayRecord[] arrayRecords, TableRecord[] tableRecords) {
        int nShF = sharedFormulaRecords.length;
        if (nShF != firstCells.length) {
            throw new IllegalArgumentException("array sizes don't match: " + nShF + "!=" + firstCells.length + ".");
        }
        _arrayRecords = toList(arrayRecords);
        _tableRecords = tableRecords;
        Map<SharedFormulaRecord, SharedFormulaGroup> m = new HashMap<SharedFormulaRecord, SharedFormulaGroup>(nShF * 3 / 2);
        for (int i = 0; i < nShF; i++) {
            SharedFormulaRecord sfr = sharedFormulaRecords[i];
            m.put(sfr, new SharedFormulaGroup(sfr, firstCells[i]));
        }
        _groupsBySharedFormulaRecord = m;
    }


    public static SharedValueManager createEmpty() {

        return new SharedValueManager(
                new SharedFormulaRecord[0], new CellReference[0], new ArrayRecord[0], new TableRecord[0]);
    }


    private static <Z> List<Z> toList(Z[] zz) {
        List<Z> result = new ArrayList<Z>(zz.length);
        for (int i = 0; i < zz.length; i++) {
            result.add(zz[i]);
        }
        return result;
    }


    @Keep
    public static SharedValueManager create(SharedFormulaRecord[] sharedFormulaRecords,
                                            CellReference[] firstCells, ArrayRecord[] arrayRecords, TableRecord[] tableRecords) {
        if (sharedFormulaRecords.length + firstCells.length + arrayRecords.length + tableRecords.length < 1) {
            return createEmpty();
        }
        return new SharedValueManager(sharedFormulaRecords, firstCells, arrayRecords, tableRecords);
    }


    public SharedFormulaRecord linkSharedFormulaRecord(CellReference firstCell, FormulaRecordAggregate agg) {
        SharedFormulaGroup result = findFormulaGroupForCell(firstCell);
        result.add(agg);
        return result.getSFR();
    }

    private SharedFormulaGroup findFormulaGroupForCell(final CellReference cellRef) {
        if (null == _groupsCache) {
            _groupsCache = new HashMap<Integer, SharedFormulaGroup>(_groupsBySharedFormulaRecord.size());
            for (SharedFormulaGroup group : _groupsBySharedFormulaRecord.values()) {
                _groupsCache.put(getKeyForCache(group._firstCell), group);
            }
        }
        SharedFormulaGroup sfg = _groupsCache.get(getKeyForCache(cellRef));
        if (null == sfg) {

            throw new RuntimeException("Failed to find a matching shared formula record");
        }
        return sfg;
    }

    private Integer getKeyForCache(final CellReference cellRef) {

        return new Integer((cellRef.getCol() + 1) << 16 | cellRef.getRow());
    }


    public SharedValueRecordBase getRecordForFirstCell(FormulaRecordAggregate agg) {
        CellReference firstCell = agg.getFormulaRecord().getFormula().getExpReference();


        if (firstCell == null) {

            return null;
        }


        int row = firstCell.getRow();
        int column = firstCell.getCol();
        if (agg.getRow() != row || agg.getColumn() != column) {

            return null;
        }

        if (!_groupsBySharedFormulaRecord.isEmpty()) {
            SharedFormulaGroup sfg = findFormulaGroupForCell(firstCell);
            if (null != sfg) {
                return sfg.getSFR();
            }
        }





        for (TableRecord tr : _tableRecords) {
            if (tr.isFirstCell(row, column)) {
                return tr;
            }
        }
        for (ArrayRecord ar : _arrayRecords) {
            if (ar.isFirstCell(row, column)) {
                return ar;
            }
        }
        return null;
    }


    public void unlink(SharedFormulaRecord sharedFormulaRecord) {
        SharedFormulaGroup svg = _groupsBySharedFormulaRecord.remove(sharedFormulaRecord);
        if (svg == null) {
            throw new IllegalStateException("Failed to find formulas for shared formula");
        }
        _groupsCache = null;
        svg.unlinkSharedFormulas();
    }


    public void addArrayRecord(ArrayRecord ar) {

        _arrayRecords.add(ar);
    }


    public CellRangeAddress8Bit removeArrayFormula(int rowIndex, int columnIndex) {
        for (ArrayRecord ar : _arrayRecords) {
            if (ar.isInRange(rowIndex, columnIndex)) {
                _arrayRecords.remove(ar);
                return ar.getRange();
            }
        }
        String ref = new CellReference(rowIndex, columnIndex, false, false).formatAsString();
        throw new IllegalArgumentException("Specified cell " + ref
                + " is not part of an array formula.");
    }


    public ArrayRecord getArrayRecord(int firstRow, int firstColumn) {
        for (ArrayRecord ar : _arrayRecords) {
            if (ar.isFirstCell(firstRow, firstColumn)) {
                return ar;
            }
        }
        return null;
    }

    private static final class SharedFormulaGroup {
        private final SharedFormulaRecord _sfr;
        private final FormulaRecordAggregate[] _frAggs;

        private final CellReference _firstCell;
        private int _numberOfFormulas;

        public SharedFormulaGroup(SharedFormulaRecord sfr, CellReference firstCell) {
            if (!sfr.isInRange(firstCell.getRow(), firstCell.getCol())) {
                throw new IllegalArgumentException("First formula cell " + firstCell.formatAsString()
                        + " is not shared formula range " + sfr.getRange().toString() + ".");
            }
            _sfr = sfr;
            _firstCell = firstCell;
            int width = sfr.getLastColumn() - sfr.getFirstColumn() + 1;
            int height = sfr.getLastRow() - sfr.getFirstRow() + 1;
            _frAggs = new FormulaRecordAggregate[width * height];
            _numberOfFormulas = 0;
        }

        public void add(FormulaRecordAggregate agg) {
            if (_numberOfFormulas == 0) {
                if (_firstCell.getRow() != agg.getRow() || _firstCell.getCol() != agg.getColumn()) {
                    throw new IllegalStateException("shared formula coding error: " + _firstCell.getCol() + '/' + _firstCell.getRow() + " != " + agg.getColumn() + '/' + agg.getRow());
                }
            }
            if (_numberOfFormulas >= _frAggs.length) {
                throw new RuntimeException("Too many formula records for shared formula group");
            }
            _frAggs[_numberOfFormulas++] = agg;
        }

        public void unlinkSharedFormulas() {
            for (int i = 0; i < _numberOfFormulas; i++) {
                _frAggs[i].unlinkSharedFormula();
            }
        }

        public SharedFormulaRecord getSFR() {
            return _sfr;
        }

        public final String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName()).append(" [");
            sb.append(_sfr.getRange().toString());
            sb.append("]");
            return sb.toString();
        }
    }
}
