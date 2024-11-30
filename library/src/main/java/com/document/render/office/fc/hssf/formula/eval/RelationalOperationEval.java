

package com.document.render.office.fc.hssf.formula.eval;


import com.document.render.office.fc.hssf.formula.function.Fixed2ArgFunction;
import com.document.render.office.fc.hssf.formula.function.Function;
import com.document.render.office.fc.ss.util.NumberComparer;



public abstract class RelationalOperationEval extends Fixed2ArgFunction {

    public static final Function EqualEval = new RelationalOperationEval() {
        protected boolean convertComparisonResult(int cmpResult) {
            return cmpResult == 0;
        }
    };
    public static final Function GreaterEqualEval = new RelationalOperationEval() {
        protected boolean convertComparisonResult(int cmpResult) {
            return cmpResult >= 0;
        }
    };
    public static final Function GreaterThanEval = new RelationalOperationEval() {
        protected boolean convertComparisonResult(int cmpResult) {
            return cmpResult > 0;
        }
    };
    public static final Function LessEqualEval = new RelationalOperationEval() {
        protected boolean convertComparisonResult(int cmpResult) {
            return cmpResult <= 0;
        }
    };
    public static final Function LessThanEval = new RelationalOperationEval() {
        protected boolean convertComparisonResult(int cmpResult) {
            return cmpResult < 0;
        }
    };
    public static final Function NotEqualEval = new RelationalOperationEval() {
        protected boolean convertComparisonResult(int cmpResult) {
            return cmpResult != 0;
        }
    };

    private static int doCompare(ValueEval va, ValueEval vb) {

        if (va == BlankEval.instance) {
            return compareBlank(vb);
        }
        if (vb == BlankEval.instance) {
            return -compareBlank(va);
        }

        if (va instanceof BoolEval) {
            if (vb instanceof BoolEval) {
                BoolEval bA = (BoolEval) va;
                BoolEval bB = (BoolEval) vb;
                if (bA.getBooleanValue() == bB.getBooleanValue()) {
                    return 0;
                }
                return bA.getBooleanValue() ? 1 : -1;
            }
            return 1;
        }
        if (vb instanceof BoolEval) {
            return -1;
        }
        if (va instanceof StringEval) {
            if (vb instanceof StringEval) {
                StringEval sA = (StringEval) va;
                StringEval sB = (StringEval) vb;
                return sA.getStringValue().compareToIgnoreCase(sB.getStringValue());
            }
            return 1;
        }
        if (vb instanceof StringEval) {
            return -1;
        }
        if (va instanceof NumberEval) {
            if (vb instanceof NumberEval) {
                NumberEval nA = (NumberEval) va;
                NumberEval nB = (NumberEval) vb;
                return NumberComparer.compare(nA.getNumberValue(), nB.getNumberValue());
            }
        }
        throw new IllegalArgumentException("Bad operand types (" + va.getClass().getName() + "), ("
                + vb.getClass().getName() + ")");
    }

    private static int compareBlank(ValueEval v) {
        if (v == BlankEval.instance) {
            return 0;
        }
        if (v instanceof BoolEval) {
            BoolEval boolEval = (BoolEval) v;
            return boolEval.getBooleanValue() ? -1 : 0;
        }
        if (v instanceof NumberEval) {
            NumberEval ne = (NumberEval) v;
            return NumberComparer.compare(0.0, ne.getNumberValue());
        }
        if (v instanceof StringEval) {
            StringEval se = (StringEval) v;
            return se.getStringValue().length() < 1 ? 0 : -1;
        }
        throw new IllegalArgumentException("bad value class (" + v.getClass().getName() + ")");
    }


    protected abstract boolean convertComparisonResult(int cmpResult);


    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {

        ValueEval vA;
        ValueEval vB;
        try {
            vA = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            vB = OperandResolver.getSingleValue(arg1, srcRowIndex, srcColumnIndex);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
        int cmpResult = doCompare(vA, vB);
        boolean result = convertComparisonResult(cmpResult);
        return BoolEval.valueOf(result);
    }
}
