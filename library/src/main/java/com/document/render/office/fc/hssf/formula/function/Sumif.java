

package com.document.render.office.fc.hssf.formula.function;


import com.document.render.office.fc.hssf.formula.eval.AreaEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.RefEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.fc.hssf.formula.function.CountUtils.I_MatchPredicate;



public final class Sumif extends Var2or3ArgFunction {

    private static ValueEval eval(int srcRowIndex, int srcColumnIndex, ValueEval arg1, AreaEval aeRange,
                                  AreaEval aeSum) {

        I_MatchPredicate mp = Countif.createCriteriaPredicate(arg1, srcRowIndex, srcColumnIndex);
        double result = sumMatchingCells(aeRange, mp, aeSum);
        return new NumberEval(result);
    }

    private static double sumMatchingCells(AreaEval aeRange, I_MatchPredicate mp, AreaEval aeSum) {
        int height = aeRange.getHeight();
        int width = aeRange.getWidth();

        double result = 0.0;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                result += accumulate(aeRange, mp, aeSum, r, c);
            }
        }
        return result;
    }

    private static double accumulate(AreaEval aeRange, I_MatchPredicate mp, AreaEval aeSum, int relRowIndex,
                                     int relColIndex) {

        if (!mp.matches(aeRange.getRelativeValue(relRowIndex, relColIndex))) {
            return 0.0;
        }
        ValueEval addend = aeSum.getRelativeValue(relRowIndex, relColIndex);
        if (addend instanceof NumberEval) {
            return ((NumberEval) addend).getNumberValue();
        }

        return 0.0;
    }


    private static AreaEval createSumRange(ValueEval eval, AreaEval aeRange) throws EvaluationException {
        if (eval instanceof AreaEval) {
            return ((AreaEval) eval).offset(0, aeRange.getHeight() - 1, 0, aeRange.getWidth() - 1);
        }
        if (eval instanceof RefEval) {
            return ((RefEval) eval).offset(0, aeRange.getHeight() - 1, 0, aeRange.getWidth() - 1);
        }
        throw new EvaluationException(ErrorEval.VALUE_INVALID);
    }

    private static AreaEval convertRangeArg(ValueEval eval) throws EvaluationException {
        if (eval instanceof AreaEval) {
            return (AreaEval) eval;
        }
        if (eval instanceof RefEval) {
            return ((RefEval) eval).offset(0, 0, 0, 0);
        }
        throw new EvaluationException(ErrorEval.VALUE_INVALID);
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {

        AreaEval aeRange;
        try {
            aeRange = convertRangeArg(arg0);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return eval(srcRowIndex, srcColumnIndex, arg1, aeRange, aeRange);
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1,
                              ValueEval arg2) {

        AreaEval aeRange;
        AreaEval aeSum;
        try {
            aeRange = convertRangeArg(arg0);
            aeSum = createSumRange(arg2, aeRange);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return eval(srcRowIndex, srcColumnIndex, arg1, aeRange, aeSum);
    }

}
