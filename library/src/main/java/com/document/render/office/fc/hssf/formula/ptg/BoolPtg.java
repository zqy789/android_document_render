

package com.document.render.office.fc.hssf.formula.ptg;

import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class BoolPtg extends ScalarConstantPtg {
    public static final int SIZE = 2;
    public static final byte sid = 0x1D;

    private static final BoolPtg FALSE = new BoolPtg(false);
    private static final BoolPtg TRUE = new BoolPtg(true);

    private final boolean _value;

    private BoolPtg(boolean b) {
        _value = b;
    }

    public static BoolPtg valueOf(boolean b) {
        return b ? TRUE : FALSE;
    }

    public static BoolPtg read(LittleEndianInput in) {
        return valueOf(in.readByte() == 1);
    }

    public boolean getValue() {
        return _value;
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(sid + getPtgClass());
        out.writeByte(_value ? 1 : 0);
    }

    public int getSize() {
        return SIZE;
    }

    public String toFormulaString() {
        return _value ? "TRUE" : "FALSE";
    }
}
