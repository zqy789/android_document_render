

package com.document.render.office.fc.ss;

import com.document.render.office.fc.ss.util.CellReference;


public enum SpreadsheetVersion {

    EXCEL97(0x10000, 0x0100, 30, 3, 32767),


    EXCEL2007(0x100000, 0x4000, 255, Integer.MAX_VALUE, 32767);

    private final int _maxRows;
    private final int _maxColumns;
    private final int _maxFunctionArgs;
    private final int _maxCondFormats;
    private final int _maxTextLength;

    private SpreadsheetVersion(int maxRows, int maxColumns, int maxFunctionArgs, int maxCondFormats, int maxText) {
        _maxRows = maxRows;
        _maxColumns = maxColumns;
        _maxFunctionArgs = maxFunctionArgs;
        _maxCondFormats = maxCondFormats;
        _maxTextLength = maxText;
    }


    public int getMaxRows() {
        return _maxRows;
    }


    public int getLastRowIndex() {
        return _maxRows - 1;
    }


    public int getMaxColumns() {
        return _maxColumns;
    }


    public int getLastColumnIndex() {
        return _maxColumns - 1;
    }


    public int getMaxFunctionArgs() {
        return _maxFunctionArgs;
    }


    public int getMaxConditionalFormats() {
        return _maxCondFormats;
    }


    public String getLastColumnName() {
        return CellReference.convertNumToColString(getLastColumnIndex());
    }


    public int getMaxTextLength() {
        return _maxTextLength;
    }

}
