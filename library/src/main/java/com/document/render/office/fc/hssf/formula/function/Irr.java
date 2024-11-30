

package com.document.render.office.fc.hssf.formula.function;

import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;


public final class Irr implements Function {


    public static double irr(double[] income) {
        return irr(income, 0.1d);
    }


    public static double irr(double[] values, double guess) {
        int maxIterationCount = 20;
        double absoluteAccuracy = 1E-7;

        double x0 = guess;
        double x1;

        int i = 0;
        while (i < maxIterationCount) {


            double fValue = 0;
            double fDerivative = 0;
            for (int k = 0; k < values.length; k++) {
                fValue += values[k] / Math.pow(1.0 + x0, k);
                fDerivative += -k * values[k] / Math.pow(1.0 + x0, k + 1);
            }


            x1 = x0 - fValue / fDerivative;

            if (Math.abs(x1 - x0) <= absoluteAccuracy) {
                return x1;
            }

            x0 = x1;
            ++i;
        }

        return Double.NaN;
    }

    public ValueEval evaluate(final ValueEval[] args, final int srcRowIndex,
                              final int srcColumnIndex) {
        if (args.length == 0 || args.length > 2) {

            return ErrorEval.VALUE_INVALID;
        }

        try {
            double[] values = AggregateFunction.ValueCollector.collectValues(args[0]);
            double guess;
            if (args.length == 2) {
                guess = NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
            } else {
                guess = 0.1d;
            }
            double result = irr(values, guess);
            NumericFunction.checkValue(result);
            return new NumberEval(result);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}
