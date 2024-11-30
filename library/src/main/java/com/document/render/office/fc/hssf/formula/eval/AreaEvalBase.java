

package com.document.render.office.fc.hssf.formula.eval;

import com.document.render.office.fc.hssf.formula.ptg.AreaI;


public abstract class AreaEvalBase implements AreaEval {

    private final int _firstColumn;
    private final int _firstRow;
    private final int _lastColumn;
    private final int _lastRow;
    private final int _nColumns;
    private final int _nRows;

    protected AreaEvalBase(int firstRow, int firstColumn, int lastRow, int lastColumn) {
        _firstColumn = firstColumn;
        _firstRow = firstRow;
        _lastColumn = lastColumn;
        _lastRow = lastRow;

        _nColumns = _lastColumn - _firstColumn + 1;
        _nRows = _lastRow - _firstRow + 1;
    }

    protected AreaEvalBase(AreaI ptg) {
        _firstRow = ptg.getFirstRow();
        _firstColumn = ptg.getFirstColumn();
        _lastRow = ptg.getLastRow();
        _lastColumn = ptg.getLastColumn();

        _nColumns = _lastColumn - _firstColumn + 1;
        _nRows = _lastRow - _firstRow + 1;
    }

    public final int getFirstColumn() {
        return _firstColumn;
    }

    public final int getFirstRow() {
        return _firstRow;
    }

    public final int getLastColumn() {
        return _lastColumn;
    }

    public final int getLastRow() {
        return _lastRow;
    }

    public final ValueEval getAbsoluteValue(int row, int col) {
        int rowOffsetIx = row - _firstRow;
        int colOffsetIx = col - _firstColumn;

        if (rowOffsetIx < 0 || rowOffsetIx >= _nRows) {
            throw new IllegalArgumentException("Specified row index (" + row
                    + ") is outside the allowed range (" + _firstRow + ".." + _lastRow + ")");
        }
        if (colOffsetIx < 0 || colOffsetIx >= _nColumns) {
            throw new IllegalArgumentException("Specified column index (" + col
                    + ") is outside the allowed range (" + _firstColumn + ".." + col + ")");
        }
        return getRelativeValue(rowOffsetIx, colOffsetIx);
    }

    public final boolean contains(int row, int col) {
        return _firstRow <= row && _lastRow >= row
                && _firstColumn <= col && _lastColumn >= col;
    }

    public final boolean containsRow(int row) {
        return _firstRow <= row && _lastRow >= row;
    }

    public final boolean containsColumn(int col) {
        return _firstColumn <= col && _lastColumn >= col;
    }

    public final boolean isColumn() {
        return _firstColumn == _lastColumn;
    }

    public final boolean isRow() {
        return _firstRow == _lastRow;
    }

    public int getHeight() {
        return _lastRow - _firstRow + 1;
    }

    public final ValueEval getValue(int row, int col) {
        return getRelativeValue(row, col);
    }

    public abstract ValueEval getRelativeValue(int relativeRowIndex, int relativeColumnIndex);

    public int getWidth() {
        return _lastColumn - _firstColumn + 1;
    }


    public boolean isSubTotal(int rowIndex, int columnIndex) {
        return false;
    }

}
