

package com.document.render.office.fc.ss.util;

import com.document.render.office.fc.ss.SpreadsheetVersion;



public abstract class CellRangeAddressBase {

    private int _firstRow;
    private int _firstCol;
    private int _lastRow;
    private int _lastCol;

    protected CellRangeAddressBase(int firstRow, int lastRow, int firstCol, int lastCol) {
        _firstRow = firstRow;
        _lastRow = lastRow;
        _firstCol = firstCol;
        _lastCol = lastCol;
    }


    private static void validateRow(int row, SpreadsheetVersion ssVersion) {
        int maxrow = ssVersion.getLastRowIndex();
        if (row > maxrow) throw new IllegalArgumentException("Maximum row number is " + maxrow);
        if (row < 0) throw new IllegalArgumentException("Minumum row number is 0");
    }


    private static void validateColumn(int column, SpreadsheetVersion ssVersion) {
        int maxcol = ssVersion.getLastColumnIndex();
        if (column > maxcol)
            throw new IllegalArgumentException("Maximum column number is " + maxcol);
        if (column < 0) throw new IllegalArgumentException("Minimum column number is 0");
    }


    public void validate(SpreadsheetVersion ssVersion) {
        validateRow(_firstRow, ssVersion);
        validateRow(_lastRow, ssVersion);
        validateColumn(_firstCol, ssVersion);
        validateColumn(_lastCol, ssVersion);
    }


    public final boolean isFullColumnRange() {
        return _firstRow == 0 && _lastRow == SpreadsheetVersion.EXCEL97.getLastRowIndex();
    }


    public final boolean isFullRowRange() {
        return _firstCol == 0 && _lastCol == SpreadsheetVersion.EXCEL97.getLastColumnIndex();
    }


    public final int getFirstColumn() {
        return _firstCol;
    }


    public final void setFirstColumn(int firstCol) {
        _firstCol = firstCol;
    }


    public final int getFirstRow() {
        return _firstRow;
    }


    public final void setFirstRow(int firstRow) {
        _firstRow = firstRow;
    }


    public final int getLastColumn() {
        return _lastCol;
    }


    public final void setLastColumn(int lastCol) {
        _lastCol = lastCol;
    }


    public final int getLastRow() {
        return _lastRow;
    }


    public final void setLastRow(int lastRow) {
        _lastRow = lastRow;
    }

    public boolean isInRange(int rowInd, int colInd) {
        return _firstRow <= rowInd && rowInd <= _lastRow &&
                _firstCol <= colInd && colInd <= _lastCol;
    }


    public int getNumberOfCells() {
        return (_lastRow - _firstRow + 1) * (_lastCol - _firstCol + 1);
    }

    public final String toString() {
        CellReference crA = new CellReference(_firstRow, _firstCol);
        CellReference crB = new CellReference(_lastRow, _lastCol);
        return getClass().getName() + " [" + crA.formatAsString() + ":" + crB.formatAsString() + "]";
    }
}
