

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
import com.document.render.office.fc.hssf.formula.function.CountUtils.I_MatchPredicate;
import com.document.render.office.fc.ss.usermodel.ErrorConstants;

import java.util.regex.Pattern;



public final class Countif extends Fixed2ArgFunction {



    static I_MatchPredicate createCriteriaPredicate(ValueEval arg, int srcRowIndex, int srcColumnIndex) {

        ValueEval evaluatedCriteriaArg = evaluateCriteriaArg(arg, srcRowIndex, srcColumnIndex);

        if (evaluatedCriteriaArg instanceof NumberEval) {
            return new NumberMatcher(((NumberEval) evaluatedCriteriaArg).getNumberValue(), CmpOp.OP_NONE);
        }
        if (evaluatedCriteriaArg instanceof BoolEval) {
            return new BooleanMatcher(((BoolEval) evaluatedCriteriaArg).getBooleanValue(), CmpOp.OP_NONE);
        }

        if (evaluatedCriteriaArg instanceof StringEval) {
            return createGeneralMatchPredicate((StringEval) evaluatedCriteriaArg);
        }
        if (evaluatedCriteriaArg instanceof ErrorEval) {
            return new ErrorMatcher(((ErrorEval) evaluatedCriteriaArg).getErrorCode(), CmpOp.OP_NONE);
        }
        if (evaluatedCriteriaArg == BlankEval.instance) {
            return null;
        }
        throw new RuntimeException("Unexpected type for criteria ("
                + evaluatedCriteriaArg.getClass().getName() + ")");
    }


    private static ValueEval evaluateCriteriaArg(ValueEval arg, int srcRowIndex, int srcColumnIndex) {
        try {
            return OperandResolver.getSingleValue(arg, srcRowIndex, (short) srcColumnIndex);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }


    private static I_MatchPredicate createGeneralMatchPredicate(StringEval stringEval) {
        String value = stringEval.getStringValue();
        CmpOp operator = CmpOp.getOperator(value);
        value = value.substring(operator.getLength());

        Boolean booleanVal = parseBoolean(value);
        if (booleanVal != null) {
            return new BooleanMatcher(booleanVal.booleanValue(), operator);
        }

        Double doubleVal = OperandResolver.parseDouble(value);
        if (doubleVal != null) {
            return new NumberMatcher(doubleVal.doubleValue(), operator);
        }
        ErrorEval ee = parseError(value);
        if (ee != null) {
            return new ErrorMatcher(ee.getErrorCode(), operator);
        }


        return new StringMatcher(value, operator);
    }

    private static ErrorEval parseError(String value) {
        if (value.length() < 4 || value.charAt(0) != '#') {
            return null;
        }
        if (value.equals("#NULL!")) return ErrorEval.NULL_INTERSECTION;
        if (value.equals("#DIV/0!")) return ErrorEval.DIV_ZERO;
        if (value.equals("#VALUE!")) return ErrorEval.VALUE_INVALID;
        if (value.equals("#REF!")) return ErrorEval.REF_INVALID;
        if (value.equals("#NAME?")) return ErrorEval.NAME_INVALID;
        if (value.equals("#NUM!")) return ErrorEval.NUM_ERROR;
        if (value.equals("#N/A")) return ErrorEval.NA;

        return null;
    }



    static Boolean parseBoolean(String strRep) {
        if (strRep.length() < 1) {
            return null;
        }
        switch (strRep.charAt(0)) {
            case 't':
            case 'T':
                if ("TRUE".equalsIgnoreCase(strRep)) {
                    return Boolean.TRUE;
                }
                break;
            case 'f':
            case 'F':
                if ("FALSE".equalsIgnoreCase(strRep)) {
                    return Boolean.FALSE;
                }
                break;
        }
        return null;
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {

        I_MatchPredicate mp = createCriteriaPredicate(arg1, srcRowIndex, srcColumnIndex);
        if (mp == null) {

            return NumberEval.ZERO;
        }
        double result = countMatchingCellsInArea(arg0, mp);
        return new NumberEval(result);
    }


    private double countMatchingCellsInArea(ValueEval rangeArg, I_MatchPredicate criteriaPredicate) {

        if (rangeArg instanceof RefEval) {
            return CountUtils.countMatchingCell((RefEval) rangeArg, criteriaPredicate);
        } else if (rangeArg instanceof TwoDEval) {
            return CountUtils.countMatchingCellsInArea((TwoDEval) rangeArg, criteriaPredicate);
        } else {
            throw new IllegalArgumentException("Bad range arg type (" + rangeArg.getClass().getName() + ")");
        }
    }

    private static final class CmpOp {
        public static final int NONE = 0;
        public static final int EQ = 1;
        public static final int NE = 2;
        public static final int LE = 3;
        public static final int LT = 4;
        public static final int GT = 5;
        public static final int GE = 6;

        public static final CmpOp OP_NONE = op("", NONE);
        public static final CmpOp OP_EQ = op("=", EQ);
        public static final CmpOp OP_NE = op("<>", NE);
        public static final CmpOp OP_LE = op("<=", LE);
        public static final CmpOp OP_LT = op("<", LT);
        public static final CmpOp OP_GT = op(">", GT);
        public static final CmpOp OP_GE = op(">=", GE);
        private final String _representation;
        private final int _code;

        private CmpOp(String representation, int code) {
            _representation = representation;
            _code = code;
        }

        private static CmpOp op(String rep, int code) {
            return new CmpOp(rep, code);
        }

        public static CmpOp getOperator(String value) {
            int len = value.length();
            if (len < 1) {
                return OP_NONE;
            }

            char firstChar = value.charAt(0);

            switch (firstChar) {
                case '=':
                    return OP_EQ;
                case '>':
                    if (len > 1) {
                        switch (value.charAt(1)) {
                            case '=':
                                return OP_GE;
                        }
                    }
                    return OP_GT;
                case '<':
                    if (len > 1) {
                        switch (value.charAt(1)) {
                            case '=':
                                return OP_LE;
                            case '>':
                                return OP_NE;
                        }
                    }
                    return OP_LT;
            }
            return OP_NONE;
        }


        public int getLength() {
            return _representation.length();
        }

        public int getCode() {
            return _code;
        }

        public boolean evaluate(boolean cmpResult) {
            switch (_code) {
                case NONE:
                case EQ:
                    return cmpResult;
                case NE:
                    return !cmpResult;
            }
            throw new RuntimeException("Cannot call boolean evaluate on non-equality operator '"
                    + _representation + "'");
        }

        public boolean evaluate(int cmpResult) {
            switch (_code) {
                case NONE:
                case EQ:
                    return cmpResult == 0;
                case NE:
                    return cmpResult != 0;
                case LT:
                    return cmpResult < 0;
                case LE:
                    return cmpResult <= 0;
                case GT:
                    return cmpResult > 0;
                case GE:
                    return cmpResult <= 0;
            }
            throw new RuntimeException("Cannot call boolean evaluate on non-equality operator '"
                    + _representation + "'");
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName());
            sb.append(" [").append(_representation).append("]");
            return sb.toString();
        }

        public String getRepresentation() {
            return _representation;
        }
    }

    private static abstract class MatcherBase implements I_MatchPredicate {
        private final CmpOp _operator;

        MatcherBase(CmpOp operator) {
            _operator = operator;
        }

        protected final int getCode() {
            return _operator.getCode();
        }

        protected final boolean evaluate(int cmpResult) {
            return _operator.evaluate(cmpResult);
        }

        protected final boolean evaluate(boolean cmpResult) {
            return _operator.evaluate(cmpResult);
        }

        @Override
        public final String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName()).append(" [");
            sb.append(_operator.getRepresentation());
            sb.append(getValueText());
            sb.append("]");
            return sb.toString();
        }

        protected abstract String getValueText();
    }

    private static final class NumberMatcher extends MatcherBase {

        private final double _value;

        public NumberMatcher(double value, CmpOp operator) {
            super(operator);
            _value = value;
        }

        @Override
        protected String getValueText() {
            return String.valueOf(_value);
        }

        public boolean matches(ValueEval x) {
            double testValue;
            if (x instanceof StringEval) {


                switch (getCode()) {
                    case CmpOp.EQ:
                    case CmpOp.NONE:
                        break;
                    case CmpOp.NE:


                        return true;
                    default:


                        return false;
                }
                StringEval se = (StringEval) x;
                Double val = OperandResolver.parseDouble(se.getStringValue());
                if (val == null) {

                    return false;
                }
                return _value == val.doubleValue();
            } else if ((x instanceof NumberEval)) {
                NumberEval ne = (NumberEval) x;
                testValue = ne.getNumberValue();
            } else {
                return false;
            }
            return evaluate(Double.compare(testValue, _value));
        }
    }

    private static final class BooleanMatcher extends MatcherBase {

        private final int _value;

        public BooleanMatcher(boolean value, CmpOp operator) {
            super(operator);
            _value = boolToInt(value);
        }

        private static int boolToInt(boolean value) {
            return value ? 1 : 0;
        }

        @Override
        protected String getValueText() {
            return _value == 1 ? "TRUE" : "FALSE";
        }

        public boolean matches(ValueEval x) {
            int testValue;
            if (x instanceof StringEval) {
                if (true) {


                    return false;
                }
                StringEval se = (StringEval) x;
                Boolean val = parseBoolean(se.getStringValue());
                if (val == null) {

                    return false;
                }
                testValue = boolToInt(val.booleanValue());
            } else if ((x instanceof BoolEval)) {
                BoolEval be = (BoolEval) x;
                testValue = boolToInt(be.getBooleanValue());
            } else {
                return false;
            }
            return evaluate(testValue - _value);
        }
    }

    private static final class ErrorMatcher extends MatcherBase {

        private final int _value;

        public ErrorMatcher(int errorCode, CmpOp operator) {
            super(operator);
            _value = errorCode;
        }

        @Override
        protected String getValueText() {
            return ErrorConstants.getText(_value);
        }

        public boolean matches(ValueEval x) {
            if (x instanceof ErrorEval) {
                int testValue = ((ErrorEval) x).getErrorCode();
                return evaluate(testValue - _value);
            }
            return false;
        }
    }

    private static final class StringMatcher extends MatcherBase {

        private final String _value;
        private final Pattern _pattern;

        public StringMatcher(String value, CmpOp operator) {
            super(operator);
            _value = value;
            switch (operator.getCode()) {
                case CmpOp.NONE:
                case CmpOp.EQ:
                case CmpOp.NE:
                    _pattern = getWildCardPattern(value);
                    break;
                default:

                    _pattern = null;
            }
        }


        private static Pattern getWildCardPattern(String value) {
            int len = value.length();
            StringBuffer sb = new StringBuffer(len);
            boolean hasWildCard = false;
            for (int i = 0; i < len; i++) {
                char ch = value.charAt(i);
                switch (ch) {
                    case '?':
                        hasWildCard = true;

                        sb.append('.');
                        continue;
                    case '*':
                        hasWildCard = true;

                        sb.append(".*");
                        continue;
                    case '~':
                        if (i + 1 < len) {
                            ch = value.charAt(i + 1);
                            switch (ch) {
                                case '?':
                                case '*':
                                    hasWildCard = true;
                                    sb.append('[').append(ch).append(']');
                                    i++;
                                    continue;
                            }
                        }

                        sb.append('~');
                        continue;
                    case '.':
                    case '$':
                    case '^':
                    case '[':
                    case ']':
                    case '(':
                    case ')':

                        sb.append("\\").append(ch);
                        continue;
                }
                sb.append(ch);
            }
            if (hasWildCard) {
                return Pattern.compile(sb.toString());
            }
            return null;
        }

        @Override
        protected String getValueText() {
            if (_pattern == null) {
                return _value;
            }
            return _pattern.pattern();
        }

        public boolean matches(ValueEval x) {
            if (x instanceof BlankEval) {
                switch (getCode()) {
                    case CmpOp.NONE:
                    case CmpOp.EQ:
                        return _value.length() == 0;
                }

                return false;
            }
            if (!(x instanceof StringEval)) {



                return false;
            }
            String testedValue = ((StringEval) x).getStringValue();
            if (testedValue.length() < 1 && _value.length() < 1) {


                switch (getCode()) {
                    case CmpOp.NONE:
                        return true;
                    case CmpOp.EQ:
                        return false;
                    case CmpOp.NE:
                        return true;
                }
                return false;
            }
            if (_pattern != null) {
                return evaluate(_pattern.matcher(testedValue).matches());
            }
            return evaluate(testedValue.compareTo(_value));
        }
    }
}
