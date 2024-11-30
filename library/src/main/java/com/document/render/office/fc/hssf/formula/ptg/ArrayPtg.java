

package com.document.render.office.fc.hssf.formula.ptg;


import com.document.render.office.constant.fc.ConstantValueParser;
import com.document.render.office.constant.fc.ErrorConstant;
import com.document.render.office.fc.ss.util.NumberToTextConverter;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class ArrayPtg extends Ptg {
    public static final byte sid = 0x20;

    private static final int RESERVED_FIELD_LEN = 7;

    public static final int PLAIN_TOKEN_SIZE = 1 + RESERVED_FIELD_LEN;


    private final int _reserved0Int;
    private final int _reserved1Short;
    private final int _reserved2Byte;


    private final int _nColumns;
    private final int _nRows;
    private final Object[] _arrayValues;

    ArrayPtg(int reserved0, int reserved1, int reserved2, int nColumns, int nRows, Object[] arrayValues) {
        _reserved0Int = reserved0;
        _reserved1Short = reserved1;
        _reserved2Byte = reserved2;
        _nColumns = nColumns;
        _nRows = nRows;
        _arrayValues = arrayValues;
    }


    public ArrayPtg(Object[][] values2d) {
        int nColumns = values2d[0].length;
        int nRows = values2d.length;

        _nColumns = (short) nColumns;
        _nRows = (short) nRows;

        Object[] vv = new Object[_nColumns * _nRows];
        for (int r = 0; r < nRows; r++) {
            Object[] rowData = values2d[r];
            for (int c = 0; c < nColumns; c++) {
                vv[getValueIndex(c, r)] = rowData[c];
            }
        }

        _arrayValues = vv;
        _reserved0Int = 0;
        _reserved1Short = 0;
        _reserved2Byte = 0;
    }

    private static String getConstantText(Object o) {

        if (o == null) {
            throw new RuntimeException("Array item cannot be null");
        }
        if (o instanceof String) {
            return "\"" + (String) o + "\"";
        }
        if (o instanceof Double) {
            return NumberToTextConverter.toText(((Double) o).doubleValue());
        }
        if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? "TRUE" : "FALSE";
        }
        if (o instanceof ErrorConstant) {
            return ((ErrorConstant) o).getText();
        }
        throw new IllegalArgumentException("Unexpected constant class (" + o.getClass().getName() + ")");
    }


    public Object[][] getTokenArrayValues() {
        if (_arrayValues == null) {
            throw new IllegalStateException("array values not read yet");
        }
        Object[][] result = new Object[_nRows][_nColumns];
        for (int r = 0; r < _nRows; r++) {
            Object[] rowData = result[r];
            for (int c = 0; c < _nColumns; c++) {
                rowData[c] = _arrayValues[getValueIndex(c, r)];
            }
        }
        return result;
    }

    public boolean isBaseToken() {
        return false;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("[ArrayPtg]\n");

        sb.append("nRows = ").append(getRowCount()).append("\n");
        sb.append("nCols = ").append(getColumnCount()).append("\n");
        if (_arrayValues == null) {
            sb.append("  #values#uninitialised#\n");
        } else {
            sb.append("  ").append(toFormulaString());
        }
        return sb.toString();
    }


     int getValueIndex(int colIx, int rowIx) {
        if (colIx < 0 || colIx >= _nColumns) {
            throw new IllegalArgumentException("Specified colIx (" + colIx
                    + ") is outside the allowed range (0.." + (_nColumns - 1) + ")");
        }
        if (rowIx < 0 || rowIx >= _nRows) {
            throw new IllegalArgumentException("Specified rowIx (" + rowIx
                    + ") is outside the allowed range (0.." + (_nRows - 1) + ")");
        }
        return rowIx * _nColumns + colIx;
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(sid + getPtgClass());
        out.writeInt(_reserved0Int);
        out.writeShort(_reserved1Short);
        out.writeByte(_reserved2Byte);
    }

    public int writeTokenValueBytes(LittleEndianOutput out) {

        out.writeByte(_nColumns - 1);
        out.writeShort(_nRows - 1);
        ConstantValueParser.encode(out, _arrayValues);
        return 3 + ConstantValueParser.getEncodedSize(_arrayValues);
    }

    public int getRowCount() {
        return _nRows;
    }

    public int getColumnCount() {
        return _nColumns;
    }


    public int getSize() {
        return PLAIN_TOKEN_SIZE

                + 1 + 2
                + ConstantValueParser.getEncodedSize(_arrayValues);
    }

    public String toFormulaString() {
        StringBuffer b = new StringBuffer();
        b.append("{");
        for (int y = 0; y < getRowCount(); y++) {
            if (y > 0) {
                b.append(";");
            }
            for (int x = 0; x < getColumnCount(); x++) {
                if (x > 0) {
                    b.append(",");
                }
                Object o = _arrayValues[getValueIndex(x, y)];
                b.append(getConstantText(o));
            }
        }
        b.append("}");
        return b.toString();
    }

    public byte getDefaultOperandClass() {
        return Ptg.CLASS_ARRAY;
    }


    static final class Initial extends Ptg {
        private final int _reserved0;
        private final int _reserved1;
        private final int _reserved2;

        public Initial(LittleEndianInput in) {
            _reserved0 = in.readInt();
            _reserved1 = in.readUShort();
            _reserved2 = in.readUByte();
        }

        private static RuntimeException invalid() {
            throw new IllegalStateException("This object is a partially initialised tArray, and cannot be used as a Ptg");
        }

        public byte getDefaultOperandClass() {
            throw invalid();
        }

        public int getSize() {
            return PLAIN_TOKEN_SIZE;
        }

        public boolean isBaseToken() {
            return false;
        }

        public String toFormulaString() {
            throw invalid();
        }

        public void write(LittleEndianOutput out) {
            throw invalid();
        }


        public ArrayPtg finishReading(LittleEndianInput in) {
            int nColumns = in.readUByte();
            short nRows = in.readShort();



            nColumns++;
            nRows++;

            int totalCount = nRows * nColumns;
            Object[] arrayValues = ConstantValueParser.parse(in, totalCount);

            ArrayPtg result = new ArrayPtg(_reserved0, _reserved1, _reserved2, nColumns, nRows, arrayValues);
            result.setClass(getPtgClass());
            return result;
        }
    }
}
