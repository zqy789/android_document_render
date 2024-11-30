

package com.document.render.office.fc.hssf.formula.eval;

import com.document.render.office.fc.hssf.formula.function.Fixed1ArgFunction;
import com.document.render.office.fc.hssf.formula.function.Function;



public final class PercentEval extends Fixed1ArgFunction {

    public static final Function instance = new PercentEval();

    private PercentEval() {

    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        double d;
        try {
            ValueEval ve = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            d = OperandResolver.coerceValueToDouble(ve);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
        if (d == 0.0) {
            return NumberEval.ZERO;
        }
        return new NumberEval(d / 100);
    }
}
