

package com.document.render.office.fc.hssf.formula.ptg;


public final class UnaryMinusPtg extends ValueOperatorPtg {
    public final static byte sid = 0x13;
    public static final ValueOperatorPtg instance = new UnaryMinusPtg();
    private final static String MINUS = "-";

    private UnaryMinusPtg() {

    }

    protected byte getSid() {
        return sid;
    }

    public int getNumberOfOperands() {
        return 1;
    }


    public String toFormulaString(String[] operands) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(MINUS);
        buffer.append(operands[0]);
        return buffer.toString();
    }
}
