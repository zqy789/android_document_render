

package com.document.render.office.fc.hssf.formula.function;


import com.document.render.office.fc.hssf.formula.TwoDEval;
import com.document.render.office.fc.hssf.formula.eval.BoolEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.RefEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;



public abstract class BooleanFunction implements Function {

    public static final Function AND = new BooleanFunction() {
        protected boolean getInitialResultValue() {
            return true;
        }

        protected boolean partialEvaluate(boolean cumulativeResult, boolean currentValue) {
            return cumulativeResult && currentValue;
        }
    };
    public static final Function OR = new BooleanFunction() {
        protected boolean getInitialResultValue() {
            return false;
        }

        protected boolean partialEvaluate(boolean cumulativeResult, boolean currentValue) {
            return cumulativeResult || currentValue;
        }
    };
    public static final Function FALSE = new Fixed0ArgFunction() {
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex) {
            return BoolEval.FALSE;
        }
    };
    public static final Function TRUE = new Fixed0ArgFunction() {
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex) {
            return BoolEval.TRUE;
        }
    };
    public static final Function NOT = new Fixed1ArgFunction() {
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            boolean boolArgVal;
            try {
                ValueEval ve = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
                Boolean b = OperandResolver.coerceValueToBoolean(ve, false);
                boolArgVal = b == null ? false : b.booleanValue();
            } catch (EvaluationException e) {
                return e.getErrorEval();
            }

            return BoolEval.valueOf(!boolArgVal);
        }
    };

    public final ValueEval evaluate(ValueEval[] args, int srcRow, int srcCol) {
        if (args.length < 1) {
            return ErrorEval.VALUE_INVALID;
        }
        boolean boolResult;
        try {
            boolResult = calculate(args);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return BoolEval.valueOf(boolResult);
    }

    private boolean calculate(ValueEval[] args) throws EvaluationException {

        boolean result = getInitialResultValue();
        boolean atleastOneNonBlank = false;


        for (int i = 0, iSize = args.length; i < iSize; i++) {
            ValueEval arg = args[i];
            if (arg instanceof TwoDEval) {
                TwoDEval ae = (TwoDEval) arg;
                int height = ae.getHeight();
                int width = ae.getWidth();
                for (int rrIx = 0; rrIx < height; rrIx++) {
                    for (int rcIx = 0; rcIx < width; rcIx++) {
                        ValueEval ve = ae.getValue(rrIx, rcIx);
                        Boolean tempVe = OperandResolver.coerceValueToBoolean(ve, true);
                        if (tempVe != null) {
                            result = partialEvaluate(result, tempVe.booleanValue());
                            atleastOneNonBlank = true;
                        }
                    }
                }
                continue;
            }
            Boolean tempVe;
            if (arg instanceof RefEval) {
                ValueEval ve = ((RefEval) arg).getInnerValueEval();
                tempVe = OperandResolver.coerceValueToBoolean(ve, true);
            } else {
                tempVe = OperandResolver.coerceValueToBoolean(arg, false);
            }


            if (tempVe != null) {
                result = partialEvaluate(result, tempVe.booleanValue());
                atleastOneNonBlank = true;
            }
        }

        if (!atleastOneNonBlank) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        return result;
    }

    protected abstract boolean getInitialResultValue();

    protected abstract boolean partialEvaluate(boolean cumulativeResult, boolean currentValue);
}
