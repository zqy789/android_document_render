

package com.document.render.office.fc.hssf.formula.ptg;



public abstract class ScalarConstantPtg extends Ptg {
    public final boolean isBaseToken() {
        return true;
    }

    public final byte getDefaultOperandClass() {
        return Ptg.CLASS_VALUE;
    }

    public final String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName()).append(" [");
        sb.append(toFormulaString());
        sb.append("]");
        return sb.toString();
    }
}
