

package com.document.render.office.fc.hssf.formula.eval;

import com.document.render.office.fc.hssf.formula.EvaluationCell;
import com.document.render.office.fc.hssf.formula.EvaluationSheet;
import com.document.render.office.fc.hssf.formula.EvaluationWorkbook;
import com.document.render.office.fc.ss.usermodel.Sheet;
import com.document.render.office.fc.ss.util.CellReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;



final class ForkedEvaluationSheet implements EvaluationSheet {

    private final EvaluationSheet _masterSheet;

    private final Map<RowColKey, ForkedEvaluationCell> _sharedCellsByRowCol;

    public ForkedEvaluationSheet(EvaluationSheet masterSheet) {
        _masterSheet = masterSheet;
        _sharedCellsByRowCol = new HashMap<RowColKey, ForkedEvaluationCell>();
    }

    public EvaluationCell getCell(int rowIndex, int columnIndex) {
        RowColKey key = new RowColKey(rowIndex, columnIndex);

        ForkedEvaluationCell result = _sharedCellsByRowCol.get(key);
        if (result == null) {
            return _masterSheet.getCell(rowIndex, columnIndex);
        }
        return result;
    }

    public ForkedEvaluationCell getOrCreateUpdatableCell(int rowIndex, int columnIndex) {
        RowColKey key = new RowColKey(rowIndex, columnIndex);

        ForkedEvaluationCell result = _sharedCellsByRowCol.get(key);
        if (result == null) {
            EvaluationCell mcell = _masterSheet.getCell(rowIndex, columnIndex);
            if (mcell == null) {
                CellReference cr = new CellReference(rowIndex, columnIndex);
                throw new UnsupportedOperationException("Underlying cell '"
                        + cr.formatAsString() + "' is missing in master sheet.");
            }
            result = new ForkedEvaluationCell(this, mcell);
            _sharedCellsByRowCol.put(key, result);
        }
        return result;
    }

    public void copyUpdatedCells(Sheet sheet) {
        RowColKey[] keys = new RowColKey[_sharedCellsByRowCol.size()];
        _sharedCellsByRowCol.keySet().toArray(keys);
        Arrays.sort(keys);














    }

    public int getSheetIndex(EvaluationWorkbook mewb) {
        return mewb.getSheetIndex(_masterSheet);
    }

    private static final class RowColKey implements Comparable<RowColKey> {
        private final int _rowIndex;
        private final int _columnIndex;

        public RowColKey(int rowIndex, int columnIndex) {
            _rowIndex = rowIndex;
            _columnIndex = columnIndex;
        }

        @Override
        public boolean equals(Object obj) {
            assert obj instanceof RowColKey : "these private cache key instances are only compared to themselves";
            RowColKey other = (RowColKey) obj;
            return _rowIndex == other._rowIndex && _columnIndex == other._columnIndex;
        }

        @Override
        public int hashCode() {
            return _rowIndex ^ _columnIndex;
        }

        public int compareTo(RowColKey o) {
            int cmp = _rowIndex - o._rowIndex;
            if (cmp != 0) {
                return cmp;
            }
            return _columnIndex - o._columnIndex;
        }

        public int getRowIndex() {
            return _rowIndex;
        }

        public int getColumnIndex() {
            return _columnIndex;
        }
    }
}
