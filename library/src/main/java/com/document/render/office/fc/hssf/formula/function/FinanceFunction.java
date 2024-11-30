

package com.document.render.office.fc.hssf.formula.function;

import com.document.render.office.fc.hssf.formula.eval.BoolEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;


public abstract class FinanceFunction implements Function3Arg, Function4Arg {
    public static final Function FV = new FinanceFunction() {
        protected double evaluate(double rate, double arg1, double arg2, double arg3, boolean type) {
            return FinanceLib.fv(rate, arg1, arg2, arg3, type);
        }
    };
    public static final Function NPER = new FinanceFunction() {
        protected double evaluate(double rate, double arg1, double arg2, double arg3, boolean type) {
            return FinanceLib.nper(rate, arg1, arg2, arg3, type);
        }
    };
    public static final Function PMT = new FinanceFunction() {
        protected double evaluate(double rate, double arg1, double arg2, double arg3, boolean type) {
            return FinanceLib.pmt(rate, arg1, arg2, arg3, type);
        }
    };
    public static final Function PV = new FinanceFunction() {
        protected double evaluate(double rate, double arg1, double arg2, double arg3, boolean type) {
            return FinanceLib.pv(rate, arg1, arg2, arg3, type);
        }
    };
    private static final ValueEval DEFAULT_ARG3 = NumberEval.ZERO;
    private static final ValueEval DEFAULT_ARG4 = BoolEval.FALSE;

    protected FinanceFunction() {

    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1,
                              ValueEval arg2) {
        return evaluate(srcRowIndex, srcColumnIndex, arg0, arg1, arg2, DEFAULT_ARG3);
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1,
                              ValueEval arg2, ValueEval arg3) {
        return evaluate(srcRowIndex, srcColumnIndex, arg0, arg1, arg2, arg3, DEFAULT_ARG4);
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1,
                              ValueEval arg2, ValueEval arg3, ValueEval arg4) {
        double result;
        try {
            double d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
            double d1 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
            double d2 = NumericFunction.singleOperandEvaluate(arg2, srcRowIndex, srcColumnIndex);
            double d3 = NumericFunction.singleOperandEvaluate(arg3, srcRowIndex, srcColumnIndex);
            double d4 = NumericFunction.singleOperandEvaluate(arg4, srcRowIndex, srcColumnIndex);
            result = evaluate(d0, d1, d2, d3, d4 != 0.0);
            NumericFunction.checkValue(result);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(result);
    }

    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        switch (args.length) {
            case 3:
                return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2], DEFAULT_ARG3, DEFAULT_ARG4);
            case 4:
                return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2], args[3], DEFAULT_ARG4);
            case 5:
                return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2], args[3], args[4]);
        }
        return ErrorEval.VALUE_INVALID;
    }

    protected double evaluate(double[] ds) throws EvaluationException {




        double arg3 = 0.0;
        double arg4 = 0.0;

        switch (ds.length) {
            case 5:
                arg4 = ds[4];
            case 4:
                arg3 = ds[3];
            case 3:
                break;
            default:
                throw new IllegalStateException("Wrong number of arguments");
        }
        return evaluate(ds[0], ds[1], ds[2], arg3, arg4 != 0.0);
    }

    protected abstract double evaluate(double rate, double arg1, double arg2, double arg3, boolean type) throws EvaluationException;
}
