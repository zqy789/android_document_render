

package com.document.render.office.fc.hssf.record;


import com.document.render.office.fc.hssf.util.CellRangeAddress8Bit;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;



public abstract class SharedValueRecordBase extends StandardRecord {

    private CellRangeAddress8Bit _range;

    protected SharedValueRecordBase(CellRangeAddress8Bit range) {
        if (range == null) {
            throw new IllegalArgumentException("range must be supplied.");
        }
        _range = range;
    }

    protected SharedValueRecordBase() {
        this(new CellRangeAddress8Bit(0, 0, 0, 0));
    }

    
    public SharedValueRecordBase(LittleEndianInput in) {
        _range = new CellRangeAddress8Bit(in);
    }

    
    public final CellRangeAddress8Bit getRange() {
        return _range;
    }

    public final int getFirstRow() {
        return _range.getFirstRow();
    }

    public final int getLastRow() {
        return _range.getLastRow();
    }

    public final int getFirstColumn() {
        return (short) _range.getFirstColumn();
    }

    public final int getLastColumn() {
        return (short) _range.getLastColumn();
    }

    protected int getDataSize() {
        return CellRangeAddress8Bit.ENCODED_SIZE + getExtraDataSize();
    }

    protected abstract int getExtraDataSize();

    protected abstract void serializeExtraData(LittleEndianOutput out);

    public void serialize(LittleEndianOutput out) {
        _range.serialize(out);
        serializeExtraData(out);
    }

    
    public final boolean isInRange(int rowIx, int colIx) {
        CellRangeAddress8Bit r = _range;
        return r.getFirstRow() <= rowIx
                && r.getLastRow() >= rowIx
                && r.getFirstColumn() <= colIx
                && r.getLastColumn() >= colIx;
    }

    
    public final boolean isFirstCell(int rowIx, int colIx) {
        CellRangeAddress8Bit r = getRange();
        return r.getFirstRow() == rowIx && r.getFirstColumn() == colIx;
    }
}
