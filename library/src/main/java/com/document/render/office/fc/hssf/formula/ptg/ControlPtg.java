

package com.document.render.office.fc.hssf.formula.ptg;


public abstract class ControlPtg extends Ptg {

    public boolean isBaseToken() {
        return true;
    }

    public final byte getDefaultOperandClass() {
        throw new IllegalStateException("Control tokens are not classified");
    }
}
