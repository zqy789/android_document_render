

package com.document.render.office.fc.hssf.formula.function;


import com.document.render.office.fc.hssf.formula.TwoDEval;
import com.document.render.office.fc.hssf.formula.eval.BlankEval;
import com.document.render.office.fc.hssf.formula.eval.BoolEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.RefEval;
import com.document.render.office.fc.hssf.formula.eval.StringEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;



public abstract class MultiOperandNumericFunction implements Function {

    static final double[] EMPTY_DOUBLE_ARRAY = {};
    private static final int DEFAULT_MAX_NUM_OPERANDS = 30;
    private final boolean _isReferenceBoolCounted;
    private final boolean _isBlankCounted;

    protected MultiOperandNumericFunction(boolean isReferenceBoolCounted, boolean isBlankCounted) {
        _isReferenceBoolCounted = isReferenceBoolCounted;
        _isBlankCounted = isBlankCounted;
    }

    public final ValueEval evaluate(ValueEval[] args, int srcCellRow, int srcCellCol) {

        double d;
        try {
            double[] values = getNumberArray(args);
            d = evaluate(values);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }

        if (Double.isNaN(d) || Double.isInfinite(d))
            return ErrorEval.NUM_ERROR;

        return new NumberEval(d);
    }

    protected abstract double evaluate(double[] values) throws EvaluationException;


    protected int getMaxNumOperands() {
        return DEFAULT_MAX_NUM_OPERANDS;
    }


    protected final double[] getNumberArray(ValueEval[] operands) throws EvaluationException {
        if (operands.length > getMaxNumOperands()) {
            throw EvaluationException.invalidValue();
        }
        DoubleList retval = new DoubleList();

        for (int i = 0, iSize = operands.length; i < iSize; i++) {
            collectValues(operands[i], retval);
        }
        return retval.toArray();
    }


    public boolean isSubtotalCounted() {
        return true;
    }


    private void collectValues(ValueEval operand, DoubleList temp) throws EvaluationException {

        if (operand instanceof TwoDEval) {
            TwoDEval ae = (TwoDEval) operand;
            int width = ae.getWidth();
            int height = ae.getHeight();
            for (int rrIx = 0; rrIx < height; rrIx++) {
                for (int rcIx = 0; rcIx < width; rcIx++) {
                    ValueEval ve = ae.getValue(rrIx, rcIx);
                    if (!isSubtotalCounted() && ae.isSubTotal(rrIx, rcIx)) continue;
                    while (ve instanceof RefEval) {
                        ve = OperandResolver.getSingleValue(ve, 0, 0);
                    }

                    collectValue(ve, true, temp);
                }
            }
            return;
        }
        if (operand instanceof RefEval) {
            RefEval re = (RefEval) operand;
            collectValue(re.getInnerValueEval(), true, temp);
            return;
        }
        collectValue(operand, false, temp);
    }

    private void collectValue(ValueEval ve, boolean isViaReference, DoubleList temp) throws EvaluationException {
        if (ve == null) {
            throw new IllegalArgumentException("ve must not be null");
        }
        if (ve instanceof NumberEval) {
            NumberEval ne = (NumberEval) ve;
            temp.add(ne.getNumberValue());
            return;
        }
        if (ve instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval) ve);
        }
        if (ve instanceof StringEval) {
            if (isViaReference) {

                return;
            }
            String s = ((StringEval) ve).getStringValue();
            Double d = OperandResolver.parseDouble(s);
            if (d == null) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            temp.add(d.doubleValue());
            return;
        }
        if (ve instanceof BoolEval) {
            if (!isViaReference || _isReferenceBoolCounted) {
                BoolEval boolEval = (BoolEval) ve;
                temp.add(boolEval.getNumberValue());
            }
            return;
        }
        if (ve == BlankEval.instance) {
            if (_isBlankCounted) {
                temp.add(0.0);
            }
            return;
        }
        throw new RuntimeException("Invalid ValueEval type passed for conversion: ("
                + ve.getClass() + ")");
    }

    private static class DoubleList {
        private double[] _array;
        private int _count;

        public DoubleList() {
            _array = new double[8];
            _count = 0;
        }

        public double[] toArray() {
            if (_count < 1) {
                return EMPTY_DOUBLE_ARRAY;
            }
            double[] result = new double[_count];
            System.arraycopy(_array, 0, result, 0, _count);
            return result;
        }

        private void ensureCapacity(int reqSize) {
            if (reqSize > _array.length) {
                int newSize = reqSize * 3 / 2;
                double[] newArr = new double[newSize];
                System.arraycopy(_array, 0, newArr, 0, _count);
                _array = newArr;
            }
        }

        public void add(double value) {
            ensureCapacity(_count + 1);
            _array[_count] = value;
            _count++;
        }
    }
}
