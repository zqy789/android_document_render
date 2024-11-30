

package com.document.render.office.fc.hssf.formula.function;


import com.document.render.office.fc.hssf.formula.TwoDEval;
import com.document.render.office.fc.hssf.formula.eval.RefEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;



final class CountUtils {

    private CountUtils() {

    }


    public static int countMatchingCellsInArea(TwoDEval areaEval, I_MatchPredicate criteriaPredicate) {
        int result = 0;

        int height = areaEval.getHeight();
        int width = areaEval.getWidth();
        for (int rrIx = 0; rrIx < height; rrIx++) {
            for (int rcIx = 0; rcIx < width; rcIx++) {
                ValueEval ve = areaEval.getValue(rrIx, rcIx);

                if (criteriaPredicate instanceof I_MatchAreaPredicate) {
                    I_MatchAreaPredicate areaPredicate = (I_MatchAreaPredicate) criteriaPredicate;
                    if (!areaPredicate.matches(areaEval, rrIx, rcIx)) continue;
                }

                if (criteriaPredicate.matches(ve)) {
                    result++;
                }
            }
        }
        return result;
    }


    public static int countMatchingCell(RefEval refEval, I_MatchPredicate criteriaPredicate) {
        if (criteriaPredicate.matches(refEval.getInnerValueEval())) {
            return 1;
        }
        return 0;
    }

    public static int countArg(ValueEval eval, I_MatchPredicate criteriaPredicate) {
        if (eval == null) {
            throw new IllegalArgumentException("eval must not be null");
        }
        if (eval instanceof TwoDEval) {
            return countMatchingCellsInArea((TwoDEval) eval, criteriaPredicate);
        }
        if (eval instanceof RefEval) {
            return CountUtils.countMatchingCell((RefEval) eval, criteriaPredicate);
        }
        return criteriaPredicate.matches(eval) ? 1 : 0;
    }


    public interface I_MatchPredicate {
        boolean matches(ValueEval x);
    }

    public interface I_MatchAreaPredicate extends I_MatchPredicate {
        boolean matches(TwoDEval x, int rowIndex, int columnIndex);
    }
}
