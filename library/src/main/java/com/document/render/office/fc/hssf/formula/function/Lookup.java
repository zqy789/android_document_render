

package com.document.render.office.fc.hssf.formula.function;


import com.document.render.office.fc.hssf.formula.TwoDEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.fc.hssf.formula.function.LookupUtils.ValueVector;



public final class Lookup extends Var2or3ArgFunction {

    private static ValueVector createVector(TwoDEval ae) {
        ValueVector result = LookupUtils.createVector(ae);
        if (result != null) {
            return result;
        }
        
        throw new RuntimeException("non-vector lookup or result areas not supported yet");
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        
        throw new RuntimeException("Two arg version of LOOKUP not supported yet");
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1,
                              ValueEval arg2) {
        try {
            ValueEval lookupValue = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            TwoDEval aeLookupVector = LookupUtils.resolveTableArrayArg(arg1);
            TwoDEval aeResultVector = LookupUtils.resolveTableArrayArg(arg2);

            ValueVector lookupVector = createVector(aeLookupVector);
            ValueVector resultVector = createVector(aeResultVector);
            if (lookupVector.getSize() > resultVector.getSize()) {
                
                throw new RuntimeException("Lookup vector and result vector of differing sizes not supported yet");
            }
            int index = LookupUtils.lookupIndexOfValue(lookupValue, lookupVector, true);
            if (index >= 0) {
                return resultVector.getItem(index);
            }

            return null;

        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}
