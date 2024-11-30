

package com.document.render.office.fc.hssf.formula.function;

import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;


abstract class Var1or2ArgFunction implements Function1Arg, Function2Arg {

    public final ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        switch (args.length) {
            case 1:
                return evaluate(srcRowIndex, srcColumnIndex, args[0]);
            case 2:
                return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1]);
        }
        return ErrorEval.VALUE_INVALID;
    }
}
