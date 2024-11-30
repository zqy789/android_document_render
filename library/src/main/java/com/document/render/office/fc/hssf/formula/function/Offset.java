

package com.document.render.office.fc.hssf.formula.function;

import com.document.render.office.fc.hssf.formula.eval.AreaEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.RefEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;


public final class Offset implements Function {

    private static final int LAST_VALID_ROW_INDEX = 0xFFFF;
    private static final int LAST_VALID_COLUMN_INDEX = 0xFF;

    private static AreaEval createOffset(BaseRef baseRef,
                                         LinearOffsetRange orRow, LinearOffsetRange orCol) throws EvaluationException {
        LinearOffsetRange absRows = orRow.normaliseAndTranslate(baseRef.getFirstRowIndex());
        LinearOffsetRange absCols = orCol.normaliseAndTranslate(baseRef.getFirstColumnIndex());

        if (absRows.isOutOfBounds(0, LAST_VALID_ROW_INDEX)) {
            throw new EvaluationException(ErrorEval.REF_INVALID);
        }
        if (absCols.isOutOfBounds(0, LAST_VALID_COLUMN_INDEX)) {
            throw new EvaluationException(ErrorEval.REF_INVALID);
        }
        return baseRef.offset(orRow.getFirstIndex(), orRow.getLastIndex(), orCol.getFirstIndex(), orCol.getLastIndex());
    }

    private static BaseRef evaluateBaseRef(ValueEval eval) throws EvaluationException {

        if (eval instanceof RefEval) {
            return new BaseRef((RefEval) eval);
        }
        if (eval instanceof AreaEval) {
            return new BaseRef((AreaEval) eval);
        }
        if (eval instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval) eval);
        }
        throw new EvaluationException(ErrorEval.VALUE_INVALID);
    }


    static int evaluateIntArg(ValueEval eval, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(eval, srcCellRow, srcCellCol);
        return OperandResolver.coerceValueToInt(ve);
    }

    public ValueEval evaluate(ValueEval[] args, int srcCellRow, int srcCellCol) {
        if (args.length < 3 || args.length > 5) {
            return ErrorEval.VALUE_INVALID;
        }

        try {
            BaseRef baseRef = evaluateBaseRef(args[0]);
            int rowOffset = evaluateIntArg(args[1], srcCellRow, srcCellCol);
            int columnOffset = evaluateIntArg(args[2], srcCellRow, srcCellCol);
            int height = baseRef.getHeight();
            int width = baseRef.getWidth();
            switch (args.length) {
                case 5:
                    width = evaluateIntArg(args[4], srcCellRow, srcCellCol);
                case 4:
                    height = evaluateIntArg(args[3], srcCellRow, srcCellCol);
            }

            if (height == 0 || width == 0) {
                return ErrorEval.REF_INVALID;
            }
            LinearOffsetRange rowOffsetRange = new LinearOffsetRange(rowOffset, height);
            LinearOffsetRange colOffsetRange = new LinearOffsetRange(columnOffset, width);
            return createOffset(baseRef, rowOffsetRange, colOffsetRange);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }


     static final class LinearOffsetRange {

        private final int _offset;
        private final int _length;

        public LinearOffsetRange(int offset, int length) {
            if (length == 0) {

                throw new RuntimeException("length may not be zero");
            }
            _offset = offset;
            _length = length;
        }

        public short getFirstIndex() {
            return (short) _offset;
        }

        public short getLastIndex() {
            return (short) (_offset + _length - 1);
        }


        public LinearOffsetRange normaliseAndTranslate(int translationAmount) {
            if (_length > 0) {
                if (translationAmount == 0) {
                    return this;
                }
                return new LinearOffsetRange(translationAmount + _offset, _length);
            }
            return new LinearOffsetRange(translationAmount + _offset + _length + 1, -_length);
        }

        public boolean isOutOfBounds(int lowValidIx, int highValidIx) {
            if (_offset < lowValidIx) {
                return true;
            }
            if (getLastIndex() > highValidIx) {
                return true;
            }
            return false;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName()).append(" [");
            sb.append(_offset).append("...").append(getLastIndex());
            sb.append("]");
            return sb.toString();
        }
    }


    private static final class BaseRef {
        private final int _firstRowIndex;
        private final int _firstColumnIndex;
        private final int _width;
        private final int _height;
        private final RefEval _refEval;
        private final AreaEval _areaEval;

        public BaseRef(RefEval re) {
            _refEval = re;
            _areaEval = null;
            _firstRowIndex = re.getRow();
            _firstColumnIndex = re.getColumn();
            _height = 1;
            _width = 1;
        }

        public BaseRef(AreaEval ae) {
            _refEval = null;
            _areaEval = ae;
            _firstRowIndex = ae.getFirstRow();
            _firstColumnIndex = ae.getFirstColumn();
            _height = ae.getLastRow() - ae.getFirstRow() + 1;
            _width = ae.getLastColumn() - ae.getFirstColumn() + 1;
        }

        public int getWidth() {
            return _width;
        }

        public int getHeight() {
            return _height;
        }

        public int getFirstRowIndex() {
            return _firstRowIndex;
        }

        public int getFirstColumnIndex() {
            return _firstColumnIndex;
        }

        public AreaEval offset(int relFirstRowIx, int relLastRowIx,
                               int relFirstColIx, int relLastColIx) {
            if (_refEval == null) {
                return _areaEval.offset(relFirstRowIx, relLastRowIx, relFirstColIx, relLastColIx);
            }
            return _refEval.offset(relFirstRowIx, relLastRowIx, relFirstColIx, relLastColIx);
        }
    }
}
