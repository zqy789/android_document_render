

package com.document.render.office.fc.hssf.formula.function;


import com.document.render.office.fc.hssf.formula.TwoDEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.NumericValueEval;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.RefEval;
import com.document.render.office.fc.hssf.formula.eval.StringEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.fc.hssf.formula.function.LookupUtils.CompareResult;
import com.document.render.office.fc.hssf.formula.function.LookupUtils.LookupValueComparer;
import com.document.render.office.fc.hssf.formula.function.LookupUtils.ValueVector;



public final class Match extends Var2or3ArgFunction {

    private static ValueEval eval(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1,
                                  double match_type) {
        boolean matchExact = match_type == 0;

        boolean findLargestLessThanOrEqual = match_type > 0;

        try {
            ValueEval lookupValue = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            ValueVector lookupRange = evaluateLookupRange(arg1);
            int index = findIndexOfValue(lookupValue, lookupRange, matchExact, findLargestLessThanOrEqual);
            return new NumberEval(index + 1);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static ValueVector evaluateLookupRange(ValueEval eval) throws EvaluationException {
        if (eval instanceof RefEval) {
            RefEval re = (RefEval) eval;
            return new SingleValueVector(re.getInnerValueEval());
        }
        if (eval instanceof TwoDEval) {
            ValueVector result = LookupUtils.createVector((TwoDEval) eval);
            if (result == null) {
                throw new EvaluationException(ErrorEval.NA);
            }
            return result;
        }


        if (eval instanceof NumericValueEval) {
            throw new EvaluationException(ErrorEval.NA);
        }
        if (eval instanceof StringEval) {
            StringEval se = (StringEval) eval;
            Double d = OperandResolver.parseDouble(se.getStringValue());
            if (d == null) {

                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }

            throw new EvaluationException(ErrorEval.NA);
        }
        throw new RuntimeException("Unexpected eval type (" + eval.getClass().getName() + ")");
    }

    private static double evaluateMatchTypeArg(ValueEval arg, int srcCellRow, int srcCellCol)
            throws EvaluationException {
        ValueEval match_type = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);

        if (match_type instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval) match_type);
        }
        if (match_type instanceof NumericValueEval) {
            NumericValueEval ne = (NumericValueEval) match_type;
            return ne.getNumberValue();
        }
        if (match_type instanceof StringEval) {
            StringEval se = (StringEval) match_type;
            Double d = OperandResolver.parseDouble(se.getStringValue());
            if (d == null) {

                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }

            return d.doubleValue();
        }
        throw new RuntimeException("Unexpected match_type type (" + match_type.getClass().getName() + ")");
    }


    private static int findIndexOfValue(ValueEval lookupValue, ValueVector lookupRange,
                                        boolean matchExact, boolean findLargestLessThanOrEqual) throws EvaluationException {

        LookupValueComparer lookupComparer = createLookupComparer(lookupValue, matchExact);

        int size = lookupRange.getSize();
        if (matchExact) {
            for (int i = 0; i < size; i++) {
                if (lookupComparer.compareTo(lookupRange.getItem(i)).isEqual()) {
                    return i;
                }
            }
            throw new EvaluationException(ErrorEval.NA);
        }

        if (findLargestLessThanOrEqual) {

            for (int i = size - 1; i >= 0; i--) {
                CompareResult cmp = lookupComparer.compareTo(lookupRange.getItem(i));
                if (cmp.isTypeMismatch()) {
                    continue;
                }
                if (!cmp.isLessThan()) {
                    return i;
                }
            }
            throw new EvaluationException(ErrorEval.NA);
        }



        for (int i = 0; i < size; i++) {
            CompareResult cmp = lookupComparer.compareTo(lookupRange.getItem(i));
            if (cmp.isEqual()) {
                return i;
            }
            if (cmp.isGreaterThan()) {
                if (i < 1) {
                    throw new EvaluationException(ErrorEval.NA);
                }
                return i - 1;
            }
        }

        throw new EvaluationException(ErrorEval.NA);
    }

    private static LookupValueComparer createLookupComparer(ValueEval lookupValue, boolean matchExact) {
        if (matchExact && lookupValue instanceof StringEval) {
            String stringValue = ((StringEval) lookupValue).getStringValue();
            if (isLookupValueWild(stringValue)) {
                throw new RuntimeException("Wildcard lookup values '" + stringValue + "' not supported yet");
            }

        }
        return LookupUtils.createLookupComparer(lookupValue);
    }

    private static boolean isLookupValueWild(String stringValue) {
        if (stringValue.indexOf('?') >= 0 || stringValue.indexOf('*') >= 0) {
            return true;
        }
        return false;
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {

        return eval(srcRowIndex, srcColumnIndex, arg0, arg1, 1.0);
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1,
                              ValueEval arg2) {

        double match_type;

        try {
            match_type = evaluateMatchTypeArg(arg2, srcRowIndex, srcColumnIndex);
        } catch (EvaluationException e) {



            return ErrorEval.REF_INVALID;
        }

        return eval(srcRowIndex, srcColumnIndex, arg0, arg1, match_type);
    }

    private static final class SingleValueVector implements ValueVector {

        private final ValueEval _value;

        public SingleValueVector(ValueEval value) {
            _value = value;
        }

        public ValueEval getItem(int index) {
            if (index != 0) {
                throw new RuntimeException("Invalid index ("
                        + index + ") only zero is allowed");
            }
            return _value;
        }

        public int getSize() {
            return 1;
        }
    }
}
