

package com.document.render.office.fc.hssf.formula.function;


import com.document.render.office.fc.hssf.formula.TwoDEval;
import com.document.render.office.fc.hssf.formula.eval.AreaEval;
import com.document.render.office.fc.hssf.formula.eval.BlankEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.NumericValueEval;
import com.document.render.office.fc.hssf.formula.eval.RefEval;
import com.document.render.office.fc.hssf.formula.eval.StringEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;



public final class Sumproduct implements Function {


    private static ValueEval evaluateSingleProduct(ValueEval[] evalArgs) throws EvaluationException {
        int maxN = evalArgs.length;

        double term = 1D;
        for (int n = 0; n < maxN; n++) {
            double val = getScalarValue(evalArgs[n]);
            term *= val;
        }
        return new NumberEval(term);
    }

    private static double getScalarValue(ValueEval arg) throws EvaluationException {

        ValueEval eval;
        if (arg instanceof RefEval) {
            RefEval re = (RefEval) arg;
            eval = re.getInnerValueEval();
        } else {
            eval = arg;
        }

        if (eval == null) {
            throw new RuntimeException("parameter may not be null");
        }
        if (eval instanceof AreaEval) {
            AreaEval ae = (AreaEval) eval;

            if (!ae.isColumn() || !ae.isRow()) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            eval = ae.getRelativeValue(0, 0);
        }

        return getProductTerm(eval, true);
    }

    private static ValueEval evaluateAreaSumProduct(ValueEval[] evalArgs) throws EvaluationException {
        int maxN = evalArgs.length;
        TwoDEval[] args = new TwoDEval[maxN];
        try {
            System.arraycopy(evalArgs, 0, args, 0, maxN);
        } catch (ArrayStoreException e) {

            return ErrorEval.VALUE_INVALID;
        }


        TwoDEval firstArg = args[0];

        int height = firstArg.getHeight();
        int width = firstArg.getWidth();


        if (!areasAllSameSize(args, height, width)) {


            for (int i = 1; i < args.length; i++) {
                throwFirstError(args[i]);
            }
            return ErrorEval.VALUE_INVALID;
        }

        double acc = 0;

        for (int rrIx = 0; rrIx < height; rrIx++) {
            for (int rcIx = 0; rcIx < width; rcIx++) {
                double term = 1D;
                for (int n = 0; n < maxN; n++) {
                    double val = getProductTerm(args[n].getValue(rrIx, rcIx), false);
                    term *= val;
                }
                acc += term;
            }
        }

        return new NumberEval(acc);
    }

    private static void throwFirstError(TwoDEval areaEval) throws EvaluationException {
        int height = areaEval.getHeight();
        int width = areaEval.getWidth();
        for (int rrIx = 0; rrIx < height; rrIx++) {
            for (int rcIx = 0; rcIx < width; rcIx++) {
                ValueEval ve = areaEval.getValue(rrIx, rcIx);
                if (ve instanceof ErrorEval) {
                    throw new EvaluationException((ErrorEval) ve);
                }
            }
        }
    }

    private static boolean areasAllSameSize(TwoDEval[] args, int height, int width) {
        for (int i = 0; i < args.length; i++) {
            TwoDEval areaEval = args[i];

            if (areaEval.getHeight() != height) {
                return false;
            }
            if (areaEval.getWidth() != width) {
                return false;
            }
        }
        return true;
    }


    private static double getProductTerm(ValueEval ve, boolean isScalarProduct) throws EvaluationException {

        if (ve instanceof BlankEval || ve == null) {


            if (isScalarProduct) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            return 0;
        }

        if (ve instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval) ve);
        }
        if (ve instanceof StringEval) {
            if (isScalarProduct) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }


            return 0;
        }
        if (ve instanceof NumericValueEval) {
            NumericValueEval nve = (NumericValueEval) ve;
            return nve.getNumberValue();
        }
        throw new RuntimeException("Unexpected value eval class ("
                + ve.getClass().getName() + ")");
    }

    public ValueEval evaluate(ValueEval[] args, int srcCellRow, int srcCellCol) {

        int maxN = args.length;

        if (maxN < 1) {
            return ErrorEval.VALUE_INVALID;
        }
        ValueEval firstArg = args[0];
        try {
            if (firstArg instanceof NumericValueEval) {
                return evaluateSingleProduct(args);
            }
            if (firstArg instanceof RefEval) {
                return evaluateSingleProduct(args);
            }
            if (firstArg instanceof TwoDEval) {
                TwoDEval ae = (TwoDEval) firstArg;
                if (ae.isRow() && ae.isColumn()) {
                    return evaluateSingleProduct(args);
                }
                return evaluateAreaSumProduct(args);
            }
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
        throw new RuntimeException("Invalid arg type for SUMPRODUCT: ("
                + firstArg.getClass().getName() + ")");
    }
}
