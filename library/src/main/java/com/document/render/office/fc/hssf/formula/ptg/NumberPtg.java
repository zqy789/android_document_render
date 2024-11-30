

package com.document.render.office.fc.hssf.formula.ptg;


import com.document.render.office.fc.ss.util.NumberToTextConverter;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class NumberPtg extends ScalarConstantPtg {
    public final static int SIZE = 9;
    public final static byte sid = 0x1f;
    private final double field_1_value;

    public NumberPtg(LittleEndianInput in) {
        this(in.readDouble());
    }


    public NumberPtg(String value) {
        this(Double.parseDouble(value));
    }

    public NumberPtg(double value) {
        field_1_value = value;
    }

    public double getValue() {
        return field_1_value;
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(sid + getPtgClass());
        out.writeDouble(getValue());
    }

    public int getSize() {
        return SIZE;
    }

    public String toFormulaString() {
        return NumberToTextConverter.toText(field_1_value);
    }
}
