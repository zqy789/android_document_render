

package com.document.render.office.fc.hssf.formula.ptg;


public final class AddPtg extends ValueOperatorPtg {
    public final static byte sid = 0x03;
    public static final ValueOperatorPtg instance = new AddPtg();
    private final static String ADD = "+";

    private AddPtg() {

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
        buffer.append(ADD);
        buffer.append(operands[1]);
        return buffer.toString();
    }
}
