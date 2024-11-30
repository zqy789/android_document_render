

package com.document.render.office.fc.hssf.formula.ptg;


import com.document.render.office.fc.ss.usermodel.ErrorConstants;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class AreaErrPtg extends OperandPtg {
    public final static byte sid = 0x2B;
    private final int unused1;
    private final int unused2;

    public AreaErrPtg() {
        unused1 = 0;
        unused2 = 0;
    }

    public AreaErrPtg(LittleEndianInput in) {

        unused1 = in.readInt();
        unused2 = in.readInt();
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(sid + getPtgClass());
        out.writeInt(unused1);
        out.writeInt(unused2);
    }

    public String toFormulaString() {
        return ErrorConstants.getText(ErrorConstants.ERROR_REF);
    }

    public byte getDefaultOperandClass() {
        return Ptg.CLASS_REF;
    }

    public int getSize() {
        return 9;
    }
}

