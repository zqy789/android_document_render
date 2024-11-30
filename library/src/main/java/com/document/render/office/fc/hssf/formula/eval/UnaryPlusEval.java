

package com.document.render.office.fc.hssf.formula.eval;

import com.document.render.office.fc.hssf.formula.function.Fixed1ArgFunction;
import com.document.render.office.fc.hssf.formula.function.Function;



public final class UnaryPlusEval extends Fixed1ArgFunction {

    public static final Function instance = new UnaryPlusEval();

    private UnaryPlusEval() {

    }

    public ValueEval evaluate(int srcCellRow, int srcCellCol, ValueEval arg0) {
        double d;
        try {
            ValueEval ve = OperandResolver.getSingleValue(arg0, srcCellRow, srcCellCol);
            if (ve instanceof StringEval) {



                return ve;
            }
            d = OperandResolver.coerceValueToDouble(ve);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(+d);
    }
}
