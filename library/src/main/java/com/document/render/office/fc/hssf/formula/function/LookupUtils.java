

package com.document.render.office.fc.hssf.formula.function;


import com.document.render.office.fc.hssf.formula.TwoDEval;
import com.document.render.office.fc.hssf.formula.WorkbookEvaluator;
import com.document.render.office.fc.hssf.formula.eval.AreaEval;
import com.document.render.office.fc.hssf.formula.eval.BlankEval;
import com.document.render.office.fc.hssf.formula.eval.BoolEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.NumericValueEval;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.RefEval;
import com.document.render.office.fc.hssf.formula.eval.StringEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;



final class LookupUtils {

    public static ValueVector createRowVector(TwoDEval tableArray, int relativeRowIndex) {
        return new RowVector(tableArray, relativeRowIndex);
    }

    public static ValueVector createColumnVector(TwoDEval tableArray, int relativeColumnIndex) {
        return new ColumnVector(tableArray, relativeColumnIndex);
    }


    public static ValueVector createVector(TwoDEval ae) {
        if (ae.isColumn()) {
            return createColumnVector(ae, 0);
        }
        if (ae.isRow()) {
            return createRowVector(ae, 0);
        }
        return null;
    }


    public static int resolveRowOrColIndexArg(ValueEval rowColIndexArg, int srcCellRow, int srcCellCol) throws EvaluationException {
        if (rowColIndexArg == null) {
            throw new IllegalArgumentException("argument must not be null");
        }

        ValueEval veRowColIndexArg;
        try {
            veRowColIndexArg = OperandResolver.getSingleValue(rowColIndexArg, srcCellRow, (short) srcCellCol);
        } catch (EvaluationException e) {

            throw EvaluationException.invalidRef();
        }
        int oneBasedIndex;
        if (veRowColIndexArg instanceof StringEval) {
            StringEval se = (StringEval) veRowColIndexArg;
            String strVal = se.getStringValue();
            Double dVal = OperandResolver.parseDouble(strVal);
            if (dVal == null) {

                throw EvaluationException.invalidRef();

            }

        }

        oneBasedIndex = OperandResolver.coerceValueToInt(veRowColIndexArg);
        if (oneBasedIndex < 1) {

            throw EvaluationException.invalidValue();
        }
        return oneBasedIndex - 1;
    }


    public static TwoDEval resolveTableArrayArg(ValueEval eval) throws EvaluationException {
        if (eval instanceof TwoDEval) {
            return (TwoDEval) eval;
        }

        if (eval instanceof RefEval) {
            RefEval refEval = (RefEval) eval;



            return refEval.offset(0, 0, 0, 0);
        }
        throw EvaluationException.invalidValue();
    }


    public static boolean resolveRangeLookupArg(ValueEval rangeLookupArg, int srcCellRow, int srcCellCol) throws EvaluationException {

        ValueEval valEval = OperandResolver.getSingleValue(rangeLookupArg, srcCellRow, srcCellCol);
        if (valEval instanceof BlankEval) {



            return false;
        }
        if (valEval instanceof BoolEval) {

            BoolEval boolEval = (BoolEval) valEval;
            return boolEval.getBooleanValue();
        }

        if (valEval instanceof StringEval) {
            String stringValue = ((StringEval) valEval).getStringValue();
            if (stringValue.length() < 1) {


                throw EvaluationException.invalidValue();
            }

            Boolean b = Countif.parseBoolean(stringValue);
            if (b != null) {

                return b.booleanValue();
            }



            throw EvaluationException.invalidValue();


        }
        if (valEval instanceof NumericValueEval) {
            NumericValueEval nve = (NumericValueEval) valEval;

            return 0.0 != nve.getNumberValue();
        }
        throw new RuntimeException("Unexpected eval type (" + valEval.getClass().getName() + ")");
    }

    public static int lookupIndexOfValue(ValueEval lookupValue, ValueVector vector, boolean isRangeLookup) throws EvaluationException {
        LookupValueComparer lookupComparer = createLookupComparer(lookupValue);
        int result;
        if (isRangeLookup) {
            result = performBinarySearch(vector, lookupComparer);
        } else {
            result = lookupIndexOfExactValue(lookupComparer, vector);
        }
        if (result < 0) {
            throw new EvaluationException(ErrorEval.NA);
        }
        return result;
    }

    public static int lookupIndexOfValue(int srcRowIndex, int srcColumnIndex, ValueEval lookupValue, ValueVector vector, boolean isRangeLookup) throws EvaluationException {
        LookupValueComparer lookupComparer = createLookupComparer(srcRowIndex, srcColumnIndex, lookupValue);
        int result;
        if (isRangeLookup) {
            result = performBinarySearch(vector, lookupComparer);
        } else {
            result = lookupIndexOfExactValue(lookupComparer, vector);
        }
        if (result < 0) {
            throw new EvaluationException(ErrorEval.NA);
        }
        return result;
    }


    private static int lookupIndexOfExactValue(LookupValueComparer lookupComparer, ValueVector vector) {


        int size = vector.getSize();
        for (int i = 0; i < size; i++) {
            if (lookupComparer.compareTo(vector.getItem(i)).isEqual()) {
                return i;
            }
        }
        return -1;
    }


    private static int performBinarySearch(ValueVector vector, LookupValueComparer lookupComparer) {

        BinarySearchIndexes bsi = new BinarySearchIndexes(vector.getSize());

        while (true) {
            int midIx = bsi.getMidIx();

            if (midIx < 0) {
                return bsi.getLowIx();
            }
            CompareResult cr = lookupComparer.compareTo(vector.getItem(midIx));
            if (cr.isTypeMismatch()) {
                int newMidIx = handleMidValueTypeMismatch(lookupComparer, vector, bsi, midIx);
                if (newMidIx < 0) {
                    continue;
                }
                midIx = newMidIx;
                cr = lookupComparer.compareTo(vector.getItem(midIx));
            }
            if (cr.isEqual()) {
                return findLastIndexInRunOfEqualValues(lookupComparer, vector, midIx, bsi.getHighIx());
            }
            bsi.narrowSearch(midIx, cr.isLessThan());
        }
    }


    private static int handleMidValueTypeMismatch(LookupValueComparer lookupComparer, ValueVector vector,
                                                  BinarySearchIndexes bsi, int midIx) {
        int newMid = midIx;
        int highIx = bsi.getHighIx();

        while (true) {
            newMid++;
            if (newMid == highIx) {


                bsi.narrowSearch(midIx, true);
                return -1;
            }
            CompareResult cr = lookupComparer.compareTo(vector.getItem(newMid));
            if (cr.isLessThan() && newMid == highIx - 1) {

                bsi.narrowSearch(midIx, true);
                return -1;


            }
            if (cr.isTypeMismatch()) {

                continue;
            }
            if (cr.isEqual()) {
                return newMid;
            }



            bsi.narrowSearch(newMid, cr.isLessThan());
            return -1;
        }
    }


    private static int findLastIndexInRunOfEqualValues(LookupValueComparer lookupComparer, ValueVector vector,
                                                       int firstFoundIndex, int maxIx) {
        for (int i = firstFoundIndex + 1; i < maxIx; i++) {
            if (!lookupComparer.compareTo(vector.getItem(i)).isEqual()) {
                return i - 1;
            }
        }
        return maxIx - 1;
    }

    public static LookupValueComparer createLookupComparer(ValueEval lookupValue) {

        if (lookupValue == BlankEval.instance) {



            return new NumberLookupComparer(NumberEval.ZERO);
        }
        if (lookupValue instanceof StringEval) {
            return new StringLookupComparer((StringEval) lookupValue);
        }
        if (lookupValue instanceof NumberEval) {
            return new NumberLookupComparer((NumberEval) lookupValue);
        }
        if (lookupValue instanceof BoolEval) {
            return new BooleanLookupComparer((BoolEval) lookupValue);
        }
        throw new IllegalArgumentException("Bad lookup value type (" + lookupValue.getClass().getName() + ")");
    }

    public static LookupValueComparer createLookupComparer(int srcRowIndex, int srcColumnIndex, ValueEval lookupValue) {
        if (lookupValue instanceof AreaEval) {
            lookupValue = WorkbookEvaluator.dereferenceResult(lookupValue, srcRowIndex, srcColumnIndex);

            return createLookupComparer(srcRowIndex, srcColumnIndex, lookupValue);
        } else {
            return createLookupComparer(lookupValue);
        }
    }



    public interface ValueVector {
        ValueEval getItem(int index);

        int getSize();
    }

    public interface LookupValueComparer {

        CompareResult compareTo(ValueEval other);
    }

    private static final class RowVector implements ValueVector {

        private final TwoDEval _tableArray;
        private final int _size;
        private final int _rowIndex;

        public RowVector(TwoDEval tableArray, int rowIndex) {
            _rowIndex = rowIndex;
            int lastRowIx = tableArray.getHeight() - 1;
            if (rowIndex < 0 || rowIndex > lastRowIx) {
                throw new IllegalArgumentException("Specified row index (" + rowIndex
                        + ") is outside the allowed range (0.." + lastRowIx + ")");
            }
            _tableArray = tableArray;
            _size = tableArray.getWidth();
        }

        public ValueEval getItem(int index) {
            if (index > _size) {
                throw new ArrayIndexOutOfBoundsException("Specified index (" + index
                        + ") is outside the allowed range (0.." + (_size - 1) + ")");
            }
            ValueEval eval = _tableArray.getValue(_rowIndex, index);

            try {
                while (eval instanceof RefEval) {
                    eval = OperandResolver.getSingleValue(eval, 0, 0);
                }

                return eval;
            } catch (EvaluationException e) {
                return e.getErrorEval();
            }

        }

        public int getSize() {
            return _size;
        }
    }

    private static final class ColumnVector implements ValueVector {

        private final TwoDEval _tableArray;
        private final int _size;
        private final int _columnIndex;

        public ColumnVector(TwoDEval tableArray, int columnIndex) {
            _columnIndex = columnIndex;
            int lastColIx = tableArray.getWidth() - 1;
            if (columnIndex < 0 || columnIndex > lastColIx) {
                throw new IllegalArgumentException("Specified column index (" + columnIndex
                        + ") is outside the allowed range (0.." + lastColIx + ")");
            }
            _tableArray = tableArray;
            _size = _tableArray.getHeight();
        }

        public ValueEval getItem(int index) {
            if (index > _size) {
                throw new ArrayIndexOutOfBoundsException("Specified index (" + index
                        + ") is outside the allowed range (0.." + (_size - 1) + ")");
            }
            return _tableArray.getValue(index, _columnIndex);
        }

        public int getSize() {
            return _size;
        }
    }


    public static final class CompareResult {
        public static final CompareResult TYPE_MISMATCH = new CompareResult(true, 0);
        public static final CompareResult LESS_THAN = new CompareResult(false, -1);
        public static final CompareResult EQUAL = new CompareResult(false, 0);
        public static final CompareResult GREATER_THAN = new CompareResult(false, +1);
        private final boolean _isTypeMismatch;
        private final boolean _isLessThan;
        private final boolean _isEqual;
        private final boolean _isGreaterThan;
        private CompareResult(boolean isTypeMismatch, int simpleCompareResult) {
            if (isTypeMismatch) {
                _isTypeMismatch = true;
                _isLessThan = false;
                _isEqual = false;
                _isGreaterThan = false;
            } else {
                _isTypeMismatch = false;
                _isLessThan = simpleCompareResult < 0;
                _isEqual = simpleCompareResult == 0;
                _isGreaterThan = simpleCompareResult > 0;
            }
        }

        public static final CompareResult valueOf(int simpleCompareResult) {
            if (simpleCompareResult < 0) {
                return LESS_THAN;
            }
            if (simpleCompareResult > 0) {
                return GREATER_THAN;
            }
            return EQUAL;
        }

        public boolean isTypeMismatch() {
            return _isTypeMismatch;
        }

        public boolean isLessThan() {
            return _isLessThan;
        }

        public boolean isEqual() {
            return _isEqual;
        }

        public boolean isGreaterThan() {
            return _isGreaterThan;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName()).append(" [");
            sb.append(formatAsString());
            sb.append("]");
            return sb.toString();
        }

        private String formatAsString() {
            if (_isTypeMismatch) {
                return "TYPE_MISMATCH";
            }
            if (_isLessThan) {
                return "LESS_THAN";
            }
            if (_isEqual) {
                return "EQUAL";
            }
            if (_isGreaterThan) {
                return "GREATER_THAN";
            }

            return "??error??";
        }
    }

    private static abstract class LookupValueComparerBase implements LookupValueComparer {

        private final Class<? extends ValueEval> _targetClass;

        protected LookupValueComparerBase(ValueEval targetValue) {
            if (targetValue == null) {
                throw new RuntimeException("targetValue cannot be null");
            }
            _targetClass = targetValue.getClass();
        }

        public final CompareResult compareTo(ValueEval other) {
            if (other == null) {
                throw new RuntimeException("compare to value cannot be null");
            }
            if (_targetClass != other.getClass()) {
                return CompareResult.TYPE_MISMATCH;
            }
            return compareSameType(other);
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName()).append(" [");
            sb.append(getValueAsString());
            sb.append("]");
            return sb.toString();
        }

        protected abstract CompareResult compareSameType(ValueEval other);


        protected abstract String getValueAsString();
    }

    private static final class StringLookupComparer extends LookupValueComparerBase {
        private String _value;

        protected StringLookupComparer(StringEval se) {
            super(se);
            _value = se.getStringValue();
        }

        protected CompareResult compareSameType(ValueEval other) {
            StringEval se = (StringEval) other;
            return CompareResult.valueOf(_value.compareToIgnoreCase(se.getStringValue()));
        }

        protected String getValueAsString() {
            return _value;
        }
    }

    private static final class NumberLookupComparer extends LookupValueComparerBase {
        private double _value;

        protected NumberLookupComparer(NumberEval ne) {
            super(ne);
            _value = ne.getNumberValue();
        }

        protected CompareResult compareSameType(ValueEval other) {
            NumberEval ne = (NumberEval) other;
            return CompareResult.valueOf(Double.compare(_value, ne.getNumberValue()));
        }

        protected String getValueAsString() {
            return String.valueOf(_value);
        }
    }

    private static final class BooleanLookupComparer extends LookupValueComparerBase {
        private boolean _value;

        protected BooleanLookupComparer(BoolEval be) {
            super(be);
            _value = be.getBooleanValue();
        }

        protected CompareResult compareSameType(ValueEval other) {
            BoolEval be = (BoolEval) other;
            boolean otherVal = be.getBooleanValue();
            if (_value == otherVal) {
                return CompareResult.EQUAL;
            }

            if (_value) {
                return CompareResult.GREATER_THAN;
            }
            return CompareResult.LESS_THAN;
        }

        protected String getValueAsString() {
            return String.valueOf(_value);
        }
    }


    private static final class BinarySearchIndexes {

        private int _lowIx;
        private int _highIx;

        public BinarySearchIndexes(int highIx) {
            _lowIx = -1;
            _highIx = highIx;
        }


        public int getMidIx() {
            int ixDiff = _highIx - _lowIx;
            if (ixDiff < 2) {
                return -1;
            }
            return _lowIx + (ixDiff / 2);
        }

        public int getLowIx() {
            return _lowIx;
        }

        public int getHighIx() {
            return _highIx;
        }

        public void narrowSearch(int midIx, boolean isLessThan) {
            if (isLessThan) {
                _highIx = midIx;
            } else {
                _lowIx = midIx;
            }
        }
    }
}


