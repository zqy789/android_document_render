

package com.document.render.office.fc.hssf.formula;


import com.document.render.office.fc.hssf.formula.IEvaluationListener.ICacheEntry;
import com.document.render.office.fc.hssf.formula.eval.BlankEval;
import com.document.render.office.fc.hssf.formula.eval.BoolEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.StringEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;



abstract class CellCacheEntry implements ICacheEntry {
    public static final CellCacheEntry[] EMPTY_ARRAY = {};

    private final FormulaCellCacheEntrySet _consumingCells;
    private ValueEval _value;


    protected CellCacheEntry() {
        _consumingCells = new FormulaCellCacheEntrySet();
    }

    private static boolean areValuesEqual(ValueEval a, ValueEval b) {
        if (a == null) {
            return false;
        }
        Class<? extends ValueEval> cls = a.getClass();
        if (cls != b.getClass()) {

            return false;
        }
        if (a == BlankEval.instance) {
            return b == a;
        }
        if (cls == NumberEval.class) {
            return ((NumberEval) a).getNumberValue() == ((NumberEval) b).getNumberValue();
        }
        if (cls == StringEval.class) {
            return ((StringEval) a).getStringValue().equals(((StringEval) b).getStringValue());
        }
        if (cls == BoolEval.class) {
            return ((BoolEval) a).getBooleanValue() == ((BoolEval) b).getBooleanValue();
        }
        if (cls == ErrorEval.class) {
            return ((ErrorEval) a).getErrorCode() == ((ErrorEval) b).getErrorCode();
        }
        throw new IllegalStateException("Unexpected value class (" + cls.getName() + ")");
    }

    protected final void clearValue() {
        _value = null;
    }

    public final boolean updateValue(ValueEval value) {
        if (value == null) {
            throw new IllegalArgumentException("Did not expect to update to null");
        }
        boolean result = !areValuesEqual(_value, value);
        _value = value;
        return result;
    }

    public final ValueEval getValue() {
        return _value;
    }

    public final void addConsumingCell(FormulaCellCacheEntry cellLoc) {
        _consumingCells.add(cellLoc);

    }

    public final FormulaCellCacheEntry[] getConsumingCells() {
        return _consumingCells.toArray();
    }

    public final void clearConsumingCell(FormulaCellCacheEntry cce) {
        if (!_consumingCells.remove(cce)) {
            throw new IllegalStateException("Specified formula cell is not consumed by this cell");
        }
    }

    public final void recurseClearCachedFormulaResults(IEvaluationListener listener) {
        if (listener == null) {
            recurseClearCachedFormulaResults();
        } else {
            listener.onClearCachedValue(this);
            recurseClearCachedFormulaResults(listener, 1);
        }
    }


    protected final void recurseClearCachedFormulaResults() {
        FormulaCellCacheEntry[] formulaCells = getConsumingCells();

        for (int i = 0; i < formulaCells.length; i++) {
            FormulaCellCacheEntry fc = formulaCells[i];
            fc.clearFormulaEntry();
            fc.recurseClearCachedFormulaResults();
        }
    }


    protected final void recurseClearCachedFormulaResults(IEvaluationListener listener, int depth) {
        FormulaCellCacheEntry[] formulaCells = getConsumingCells();

        listener.sortDependentCachedValues(formulaCells);
        for (int i = 0; i < formulaCells.length; i++) {
            FormulaCellCacheEntry fc = formulaCells[i];
            listener.onClearDependentCachedValue(fc, depth);
            fc.clearFormulaEntry();
            fc.recurseClearCachedFormulaResults(listener, depth + 1);
        }
    }
}
