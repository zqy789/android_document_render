

package com.document.render.office.fc.hssf.formula.function;


import com.document.render.office.fc.hssf.formula.TwoDEval;
import com.document.render.office.fc.hssf.formula.eval.BoolEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.fc.hssf.formula.function.LookupUtils.ValueVector;


public final class Vlookup extends Var3or4ArgFunction {
    private static final ValueEval DEFAULT_ARG3 = BoolEval.TRUE;

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1,
                              ValueEval arg2) {
        return evaluate(srcRowIndex, srcColumnIndex, arg0, arg1, arg2, DEFAULT_ARG3);
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1,
                              ValueEval arg2, ValueEval arg3) {
        try {


            ValueEval lookupValue = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            TwoDEval tableArray = LookupUtils.resolveTableArrayArg(arg1);
            boolean isRangeLookup = LookupUtils.resolveRangeLookupArg(arg3, srcRowIndex, srcColumnIndex);
            int rowIndex = LookupUtils.lookupIndexOfValue(srcRowIndex, srcColumnIndex, lookupValue, LookupUtils.createColumnVector(tableArray, 0), isRangeLookup);
            int colIndex = LookupUtils.resolveRowOrColIndexArg(arg2, srcRowIndex, srcColumnIndex);
            ValueVector resultCol = createResultColumnVector(tableArray, colIndex);
            return resultCol.getItem(rowIndex);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }



    private ValueVector createResultColumnVector(TwoDEval tableArray, int colIndex) throws EvaluationException {
        if (colIndex >= tableArray.getWidth()) {
            throw EvaluationException.invalidRef();
        }
        return LookupUtils.createColumnVector(tableArray, colIndex);
    }
}
