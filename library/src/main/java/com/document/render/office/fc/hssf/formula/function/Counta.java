

package com.document.render.office.fc.hssf.formula.function;


import com.document.render.office.fc.hssf.formula.TwoDEval;
import com.document.render.office.fc.hssf.formula.eval.BlankEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.fc.hssf.formula.function.CountUtils.I_MatchAreaPredicate;
import com.document.render.office.fc.hssf.formula.function.CountUtils.I_MatchPredicate;



public final class Counta implements Function {
    private static final I_MatchPredicate defaultPredicate = new I_MatchPredicate() {

        public boolean matches(ValueEval valueEval) {
            
            
            

            if (valueEval == BlankEval.instance) {
                return false;
            }
            
            return true;
        }
    };
    private static final I_MatchPredicate subtotalPredicate = new I_MatchAreaPredicate() {
        public boolean matches(ValueEval valueEval) {
            return defaultPredicate.matches(valueEval);
        }

        
        public boolean matches(TwoDEval areEval, int rowIndex, int columnIndex) {
            return !areEval.isSubTotal(rowIndex, columnIndex);
        }
    };
    private final I_MatchPredicate _predicate;

    public Counta() {
        _predicate = defaultPredicate;
    }

    private Counta(I_MatchPredicate criteriaPredicate) {
        _predicate = criteriaPredicate;
    }

    public static Counta subtotalInstance() {
        return new Counta(subtotalPredicate);
    }

    public ValueEval evaluate(ValueEval[] args, int srcCellRow, int srcCellCol) {
        int nArgs = args.length;
        if (nArgs < 1) {
            
            return ErrorEval.VALUE_INVALID;
        }

        if (nArgs > 30) {
            
            return ErrorEval.VALUE_INVALID;
        }

        int temp = 0;

        for (int i = 0; i < nArgs; i++) {
            temp += CountUtils.countArg(args[i], _predicate);

        }
        return new NumberEval(temp);
    }

}
