

package com.document.render.office.fc.hssf.formula.ptg;

import com.document.render.office.fc.util.LittleEndianOutput;



public final class UnionPtg extends OperationPtg {
    public final static byte sid = 0x10;

    public static final OperationPtg instance = new UnionPtg();

    private UnionPtg() {

    }

    public final boolean isBaseToken() {
        return true;
    }

    public int getSize() {
        return 1;
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(sid + getPtgClass());
    }

    public String toFormulaString() {
        return ",";
    }



    public String toFormulaString(String[] operands) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(operands[0]);
        buffer.append(",");
        buffer.append(operands[1]);
        return buffer.toString();
    }

    public int getNumberOfOperands() {
        return 2;
    }

}
