

package com.document.render.office.fc.hssf.formula.ptg;

import com.document.render.office.fc.util.LittleEndianOutput;


public final class MissingArgPtg extends ScalarConstantPtg {

    public final static byte sid = 0x16;
    public static final Ptg instance = new MissingArgPtg();
    private final static int SIZE = 1;

    private MissingArgPtg() {

    }

    public void write(LittleEndianOutput out) {
        out.writeByte(sid + getPtgClass());
    }

    public int getSize() {
        return SIZE;
    }

    public String toFormulaString() {
        return " ";
    }
}
