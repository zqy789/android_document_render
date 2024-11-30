

package com.document.render.office.fc.hssf.formula.atp;


import com.document.render.office.fc.hssf.formula.OperationEvaluationContext;
import com.document.render.office.fc.hssf.formula.eval.BoolEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.fc.hssf.formula.function.FreeRefFunction;


final class ParityFunction implements FreeRefFunction {

    public static final FreeRefFunction IS_EVEN = new ParityFunction(0);
    public static final FreeRefFunction IS_ODD = new ParityFunction(1);
    private final int _desiredParity;

    private ParityFunction(int desiredParity) {
        _desiredParity = desiredParity;
    }

    private static int evaluateArgParity(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, (short) srcCellCol);

        double d = OperandResolver.coerceValueToDouble(ve);
        if (d < 0) {
            d = -d;
        }
        long v = (long) Math.floor(d);
        return (int) (v & 0x0001);
    }

    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length != 1) {
            return ErrorEval.VALUE_INVALID;
        }

        int val;
        try {
            val = evaluateArgParity(args[0], ec.getRowIndex(), ec.getColumnIndex());
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }

        return BoolEval.valueOf(val == _desiredParity);
    }
}
