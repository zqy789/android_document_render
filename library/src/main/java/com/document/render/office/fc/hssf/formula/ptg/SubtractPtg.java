

package com.document.render.office.fc.hssf.formula.ptg;


public final class SubtractPtg extends ValueOperatorPtg {
    public final static byte sid = 0x04;

    public static final ValueOperatorPtg instance = new SubtractPtg();

    private SubtractPtg() {

    }

    protected byte getSid() {
        return sid;
    }

    public int getNumberOfOperands() {
        return 2;
    }

    public String toFormulaString(String[] operands) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(operands[0]);
        buffer.append("-");
        buffer.append(operands[1]);
        return buffer.toString();
    }
}
