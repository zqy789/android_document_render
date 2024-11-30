

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;


public final class MulBlankRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x00BE;

    private final int _row;
    private final int _firstCol;
    private final short[] _xfs;
    private final int _lastCol;

    public MulBlankRecord(int row, int firstCol, short[] xfs) {
        _row = row;
        _firstCol = firstCol;
        _xfs = xfs;
        _lastCol = firstCol + xfs.length - 1;
    }


    public MulBlankRecord(RecordInputStream in) {
        _row = in.readUShort();
        _firstCol = in.readShort();
        _xfs = parseXFs(in);
        _lastCol = in.readShort();
    }

    private static short[] parseXFs(RecordInputStream in) {
        short[] retval = new short[(in.remaining() - 2) / 2];

        for (int idx = 0; idx < retval.length; idx++) {
            retval[idx] = in.readShort();
        }
        return retval;
    }


    public int getRow() {
        return _row;
    }


    public int getFirstColumn() {
        return _firstCol;
    }


    public int getLastColumn() {
        return _lastCol;
    }


    public int getNumColumns() {
        return _lastCol - _firstCol + 1;
    }


    public short getXFAt(int coffset) {
        return _xfs[coffset];
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[MULBLANK]\n");
        buffer.append("row  = ").append(Integer.toHexString(getRow())).append("\n");
        buffer.append("firstcol  = ").append(Integer.toHexString(getFirstColumn())).append("\n");
        buffer.append(" lastcol  = ").append(Integer.toHexString(_lastCol)).append("\n");
        for (int k = 0; k < getNumColumns(); k++) {
            buffer.append("xf").append(k).append("		= ").append(
                    Integer.toHexString(getXFAt(k))).append("\n");
        }
        buffer.append("[/MULBLANK]\n");
        return buffer.toString();
    }

    public short getSid() {
        return sid;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(_row);
        out.writeShort(_firstCol);
        int nItems = _xfs.length;
        for (int i = 0; i < nItems; i++) {
            out.writeShort(_xfs[i]);
        }
        out.writeShort(_lastCol);
    }

    protected int getDataSize() {

        return 6 + _xfs.length * 2;
    }

    @Override
    public Object clone() {

        return this;
    }
}
