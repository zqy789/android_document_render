
package com.document.render.office.fc.hssf.formula.atp;


import com.document.render.office.fc.hssf.formula.OperationEvaluationContext;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.fc.hssf.formula.function.FreeRefFunction;



final class RandBetween implements FreeRefFunction {

    public static final FreeRefFunction instance = new RandBetween();

    private RandBetween() {

    }


    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {

        double bottom, top;

        if (args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }

        try {
            bottom = OperandResolver.coerceValueToDouble(OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex()));
            top = OperandResolver.coerceValueToDouble(OperandResolver.getSingleValue(args[1], ec.getRowIndex(), ec.getColumnIndex()));
            if (bottom > top) {
                return ErrorEval.NUM_ERROR;
            }
        } catch (EvaluationException e) {
            return ErrorEval.VALUE_INVALID;
        }

        bottom = Math.ceil(bottom);
        top = Math.floor(top);

        if (bottom > top) {
            top = bottom;
        }

        return new NumberEval((bottom + (int) (Math.random() * ((top - bottom) + 1))));

    }

}
