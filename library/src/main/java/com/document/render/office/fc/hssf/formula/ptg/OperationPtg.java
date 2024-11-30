

package com.document.render.office.fc.hssf.formula.ptg;


public abstract class OperationPtg extends Ptg {
    public final static int TYPE_UNARY = 0;
    public final static int TYPE_BINARY = 1;
    public final static int TYPE_FUNCTION = 2;


    public abstract String toFormulaString(String[] operands);


    public abstract int getNumberOfOperands();

    public byte getDefaultOperandClass() {
        return Ptg.CLASS_VALUE;
    }
}
