

package com.document.render.office.fc.hssf.formula.eval;


public final class BoolEval implements NumericValueEval, StringValueEval {

    public static final BoolEval FALSE = new BoolEval(false);
    public static final BoolEval TRUE = new BoolEval(true);
    private boolean _value;

    private BoolEval(boolean value) {
        _value = value;
    }


    public static final BoolEval valueOf(boolean b) {
        return b ? TRUE : FALSE;
    }

    public boolean getBooleanValue() {
        return _value;
    }

    public double getNumberValue() {
        return _value ? 1 : 0;
    }

    public String getStringValue() {
        return _value ? "TRUE" : "FALSE";
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        sb.append(getClass().getName()).append(" [");
        sb.append(getStringValue());
        sb.append("]");
        return sb.toString();
    }
}
