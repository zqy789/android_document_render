

package com.document.render.office.fc.hssf.formula.ptg;

import com.document.render.office.fc.util.LittleEndianOutput;


public final class ParenthesisPtg extends ControlPtg {

    public final static byte sid = 0x15;
    public static final ControlPtg instance = new ParenthesisPtg();
    private final static int SIZE = 1;

    private ParenthesisPtg() {

    }

    public void write(LittleEndianOutput out) {
        out.writeByte(sid + getPtgClass());
    }

    public int getSize() {
        return SIZE;
    }

    public String toFormulaString() {
        return "()";
    }

    public String toFormulaString(String[] operands) {
        return "(" + operands[0] + ")";
    }
}
