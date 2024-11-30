

package com.document.render.office.fc.hssf.formula.ptg;

import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class IntPtg extends ScalarConstantPtg {
    public final static int SIZE = 3;
    public final static byte sid = 0x1e;

    private static final int MIN_VALUE = 0x0000;
    private static final int MAX_VALUE = 0xFFFF;
    private final int field_1_value;
    public IntPtg(LittleEndianInput in) {
        this(in.readUShort());
    }

    public IntPtg(int value) {
        if (!isInRange(value)) {
            throw new IllegalArgumentException("value is out of range: " + value);
        }
        field_1_value = value;
    }


    public static boolean isInRange(int i) {
        return i >= MIN_VALUE && i <= MAX_VALUE;
    }

    public int getValue() {
        return field_1_value;
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(sid + getPtgClass());
        out.writeShort(getValue());
    }

    public int getSize() {
        return SIZE;
    }

    public String toFormulaString() {
        return String.valueOf(getValue());
    }
}
