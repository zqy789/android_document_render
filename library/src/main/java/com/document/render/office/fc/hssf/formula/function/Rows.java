

package com.document.render.office.fc.hssf.formula.function;


import com.document.render.office.fc.hssf.formula.TwoDEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.RefEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;



public final class Rows extends Fixed1ArgFunction {

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {

        int result;
        if (arg0 instanceof TwoDEval) {
            result = ((TwoDEval) arg0).getHeight();
        } else if (arg0 instanceof RefEval) {
            result = 1;
        } else {
            return ErrorEval.VALUE_INVALID;
        }
        return new NumberEval(result);
    }
}
