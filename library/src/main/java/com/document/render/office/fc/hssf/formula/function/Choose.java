

package com.document.render.office.fc.hssf.formula.function;

import com.document.render.office.fc.hssf.formula.eval.BlankEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.MissingArgEval;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;


public final class Choose implements Function {

    public static int evaluateFirstArg(ValueEval arg0, int srcRowIndex, int srcColumnIndex)
            throws EvaluationException {
        ValueEval ev = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
        return OperandResolver.coerceValueToInt(ev);
    }

    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        if (args.length < 2) {
            return ErrorEval.VALUE_INVALID;
        }

        try {
            int ix = evaluateFirstArg(args[0], srcRowIndex, srcColumnIndex);
            if (ix < 1 || ix >= args.length) {
                return ErrorEval.VALUE_INVALID;
            }
            ValueEval result = OperandResolver.getSingleValue(args[ix], srcRowIndex, srcColumnIndex);
            if (result == MissingArgEval.instance) {
                return BlankEval.instance;
            }
            return result;
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}
