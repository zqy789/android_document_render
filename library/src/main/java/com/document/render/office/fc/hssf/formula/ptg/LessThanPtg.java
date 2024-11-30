

package com.document.render.office.fc.hssf.formula.ptg;


public final class LessThanPtg extends ValueOperatorPtg {

    public final static byte sid = 0x09;
    public static final ValueOperatorPtg instance = new LessThanPtg();

    private final static String LESSTHAN = "<";

    private LessThanPtg() {

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
        buffer.append(LESSTHAN);
        buffer.append(operands[1]);
        return buffer.toString();
    }
}
