

package com.document.render.office.fc.hssf.formula.function;


import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;


public final class Npv implements Function {

    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        int nArgs = args.length;
        if (nArgs < 2) {
            return ErrorEval.VALUE_INVALID;
        }

        try {
            double rate = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);

            ValueEval[] vargs = new ValueEval[args.length - 1];
            System.arraycopy(args, 1, vargs, 0, vargs.length);
            double[] values = AggregateFunction.ValueCollector.collectValues(vargs);

            double result = FinanceLib.npv(rate, values);
            NumericFunction.checkValue(result);
            return new NumberEval(result);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}
