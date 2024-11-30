

package com.document.render.office.fc.hssf.formula.eval;


public final class NameEval implements ValueEval {

    private final String _functionName;

    
    public NameEval(String functionName) {
        _functionName = functionName;
    }


    public String getFunctionName() {
        return _functionName;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName()).append(" [");
        sb.append(_functionName);
        sb.append("]");
        return sb.toString();
    }
}
