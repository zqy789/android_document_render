

package com.document.render.office.fc.hssf.formula.ptg;


public final class MultiplyPtg extends ValueOperatorPtg {
    public final static byte sid = 0x05;

    public static final ValueOperatorPtg instance = new MultiplyPtg();

    private MultiplyPtg() {

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
        buffer.append("*");
        buffer.append(operands[1]);
        return buffer.toString();
    }
}
