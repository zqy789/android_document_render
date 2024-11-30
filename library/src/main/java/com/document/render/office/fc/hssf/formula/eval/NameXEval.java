

package com.document.render.office.fc.hssf.formula.eval;

import com.document.render.office.fc.hssf.formula.ptg.NameXPtg;


public final class NameXEval implements ValueEval {

    private final NameXPtg _ptg;

    public NameXEval(NameXPtg ptg) {
        _ptg = ptg;
    }

    public NameXPtg getPtg() {
        return _ptg;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName()).append(" [");
        sb.append(_ptg.getSheetRefIndex()).append(", ").append(_ptg.getNameIndex());
        sb.append("]");
        return sb.toString();
    }
}
