

package com.document.render.office.fc.hssf.formula.ptg;


import com.document.render.office.fc.ss.util.AreaReference;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;



public abstract class Area2DPtgBase extends AreaPtgBase {
    private final static int SIZE = 9;

    protected Area2DPtgBase(int firstRow, int lastRow, int firstColumn, int lastColumn, boolean firstRowRelative, boolean lastRowRelative, boolean firstColRelative, boolean lastColRelative) {
        super(firstRow, lastRow, firstColumn, lastColumn, firstRowRelative, lastRowRelative, firstColRelative, lastColRelative);
    }

    protected Area2DPtgBase(AreaReference ar) {
        super(ar);
    }

    protected Area2DPtgBase(LittleEndianInput in) {
        readCoordinates(in);
    }

    protected abstract byte getSid();

    public final void write(LittleEndianOutput out) {
        out.writeByte(getSid() + getPtgClass());
        writeCoordinates(out);
    }

    public final int getSize() {
        return SIZE;
    }

    public final String toFormulaString() {
        return formatReferenceAsString();
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
