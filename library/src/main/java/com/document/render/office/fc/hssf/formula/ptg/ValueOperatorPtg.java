

package com.document.render.office.fc.hssf.formula.ptg;

import com.document.render.office.fc.util.LittleEndianOutput;


public abstract class ValueOperatorPtg extends OperationPtg {


    public final boolean isBaseToken() {
        return true;
    }

    public final byte getDefaultOperandClass() {
        return Ptg.CLASS_VALUE;
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(getSid());
    }

    protected abstract byte getSid();

    public final int getSize() {
        return 1;
    }

    public final String toFormulaString() {

        throw new RuntimeException("toFormulaString(String[] operands) should be used for subclasses of OperationPtgs");
    }
}
