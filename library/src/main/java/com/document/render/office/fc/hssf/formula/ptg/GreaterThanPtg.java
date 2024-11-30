

package com.document.render.office.fc.hssf.formula.ptg;



public final class GreaterThanPtg extends ValueOperatorPtg {
    public final static byte sid = 0x0D;
    public static final ValueOperatorPtg instance = new GreaterThanPtg();
    private final static String GREATERTHAN = ">";

    private GreaterThanPtg() {

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
        buffer.append(GREATERTHAN);
        buffer.append(operands[1]);
        return buffer.toString();
    }
}
