

package com.document.render.office.fc.hssf.formula.ptg;


public final class PercentPtg extends ValueOperatorPtg {
    public final static int SIZE = 1;
    public final static byte sid = 0x14;
    public static final ValueOperatorPtg instance = new PercentPtg();
    private final static String PERCENT = "%";

    private PercentPtg() {

    }

    protected byte getSid() {
        return sid;
    }

    public int getNumberOfOperands() {
        return 1;
    }

    public String toFormulaString(String[] operands) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(operands[0]);
        buffer.append(PERCENT);
        return buffer.toString();
    }
}
