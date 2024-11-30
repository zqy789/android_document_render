

package com.document.render.office.fc.hssf.formula.function;

import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.MissingArgEval;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;


public final class TimeFunc extends Fixed3ArgFunction {

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = 3600;
    private static final int HOURS_PER_DAY = 24;
    private static final int SECONDS_PER_DAY = HOURS_PER_DAY * SECONDS_PER_HOUR;

    private static int evalArg(ValueEval arg, int srcRowIndex, int srcColumnIndex) throws EvaluationException {
        if (arg == MissingArgEval.instance) {
            return 0;
        }
        ValueEval ev = OperandResolver.getSingleValue(arg, srcRowIndex, srcColumnIndex);

        return OperandResolver.coerceValueToInt(ev);
    }


    private static double evaluate(int hours, int minutes, int seconds) throws EvaluationException {

        if (hours > 32767 || minutes > 32767 || seconds > 32767) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        int totalSeconds = hours * SECONDS_PER_HOUR + minutes * SECONDS_PER_MINUTE + seconds;

        if (totalSeconds < 0) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        return (totalSeconds % SECONDS_PER_DAY) / (double) SECONDS_PER_DAY;
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1,
                              ValueEval arg2) {
        double result;
        try {
            result = evaluate(evalArg(arg0, srcRowIndex, srcColumnIndex), evalArg(arg1, srcRowIndex, srcColumnIndex), evalArg(arg2, srcRowIndex, srcColumnIndex));
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(result);
    }
}
