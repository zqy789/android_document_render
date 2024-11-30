

package com.document.render.office.fc.hssf.formula.ptg;


import com.document.render.office.fc.ss.util.CellReference;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;



abstract class Ref2DPtgBase extends RefPtgBase {
    private final static int SIZE = 5;


    protected Ref2DPtgBase(int row, int column, boolean isRowRelative, boolean isColumnRelative) {
        setRow(row);
        setColumn(column);
        setRowRelative(isRowRelative);
        setColRelative(isColumnRelative);
    }

    protected Ref2DPtgBase(LittleEndianInput in) {
        readCoordinates(in);
    }

    protected Ref2DPtgBase(CellReference cr) {
        super(cr);
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(getSid() + getPtgClass());
        writeCoordinates(out);
    }

    public final String toFormulaString() {
        return formatReferenceAsString();
    }

    protected abstract byte getSid();

    public final int getSize() {
        return SIZE;
    }

    public final String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName());
        sb.append(" [");
        sb.append(formatReferenceAsString());
        sb.append("]");
        return sb.toString();
    }
}
