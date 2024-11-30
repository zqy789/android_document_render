

package com.document.render.office.fc.ss.usermodel;

import com.document.render.office.fc.hssf.formula.eval.ErrorEval;


public final class CellValue {
    public static final CellValue TRUE = new CellValue(ICell.CELL_TYPE_BOOLEAN, 0.0, true, null, 0);
    public static final CellValue FALSE = new CellValue(ICell.CELL_TYPE_BOOLEAN, 0.0, false, null, 0);

    private final int _cellType;
    private final double _numberValue;
    private final boolean _booleanValue;
    private final String _textValue;
    private final int _errorCode;

    private CellValue(int cellType, double numberValue, boolean booleanValue,
                      String textValue, int errorCode) {
        _cellType = cellType;
        _numberValue = numberValue;
        _booleanValue = booleanValue;
        _textValue = textValue;
        _errorCode = errorCode;
    }


    public CellValue(double numberValue) {
        this(ICell.CELL_TYPE_NUMERIC, numberValue, false, null, 0);
    }

    public CellValue(String stringValue) {
        this(ICell.CELL_TYPE_STRING, 0.0, false, stringValue, 0);
    }

    public static CellValue valueOf(boolean booleanValue) {
        return booleanValue ? TRUE : FALSE;
    }

    public static CellValue getError(int errorCode) {
        return new CellValue(ICell.CELL_TYPE_ERROR, 0.0, false, null, errorCode);
    }



    public boolean getBooleanValue() {
        return _booleanValue;
    }


    public double getNumberValue() {
        return _numberValue;
    }


    public String getStringValue() {
        return _textValue;
    }


    public int getCellType() {
        return _cellType;
    }


    public byte getErrorValue() {
        return (byte) _errorCode;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName()).append(" [");
        sb.append(formatAsString());
        sb.append("]");
        return sb.toString();
    }

    public String formatAsString() {
        switch (_cellType) {
            case ICell.CELL_TYPE_NUMERIC:
                return String.valueOf(_numberValue);
            case ICell.CELL_TYPE_STRING:
                return '"' + _textValue + '"';
            case ICell.CELL_TYPE_BOOLEAN:
                return _booleanValue ? "TRUE" : "FALSE";
            case ICell.CELL_TYPE_ERROR:
                return ErrorEval.getText(_errorCode);
        }
        return "<error unexpected cell type " + _cellType + ">";
    }
}
