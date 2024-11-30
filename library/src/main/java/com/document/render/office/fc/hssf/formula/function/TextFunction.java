

package com.document.render.office.fc.hssf.formula.function;

import com.document.render.office.fc.hssf.formula.eval.BoolEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.StringEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;



public abstract class TextFunction implements Function {


    public static final Function CHAR = new Fixed1ArgFunction() {
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            int arg;
            try {
                arg = evaluateIntArg(arg0, srcRowIndex, srcColumnIndex);
                if (arg < 0 || arg >= 256) {
                    throw new EvaluationException(ErrorEval.VALUE_INVALID);
                }

            } catch (EvaluationException e) {
                return e.getErrorEval();
            }
            return new StringEval(String.valueOf((char) arg));
        }
    };
    public static final Function CODE = new SingleArgTextFunc() {
        protected ValueEval evaluate(String arg) {
            return new NumberEval(arg.codePointAt(0));
        }
    };
    public static final Function LEN = new SingleArgTextFunc() {
        protected ValueEval evaluate(String arg) {
            return new NumberEval(arg.length());
        }
    };
    public static final Function LOWER = new SingleArgTextFunc() {
        protected ValueEval evaluate(String arg) {
            return new StringEval(arg.toLowerCase());
        }
    };
    public static final Function UPPER = new SingleArgTextFunc() {
        protected ValueEval evaluate(String arg) {
            return new StringEval(arg.toUpperCase());
        }
    };

    public static final Function TRIM = new SingleArgTextFunc() {
        protected ValueEval evaluate(String arg) {
            return new StringEval(arg.trim());
        }
    };



    public static final Function CLEAN = new SingleArgTextFunc() {
        protected ValueEval evaluate(String arg) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < arg.length(); i++) {
                char c = arg.charAt(i);
                if (isPrintable(c)) {
                    result.append(c);
                }
            }
            return new StringEval(result.toString());
        }


        private boolean isPrintable(char c) {
            int charCode = (int) c;
            return charCode >= 32;
        }
    };

    public static final Function MID = new Fixed3ArgFunction() {

        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0,
                                  ValueEval arg1, ValueEval arg2) {
            String text;
            int startCharNum;
            int numChars;
            try {
                text = evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                startCharNum = evaluateIntArg(arg1, srcRowIndex, srcColumnIndex);
                numChars = evaluateIntArg(arg2, srcRowIndex, srcColumnIndex);
            } catch (EvaluationException e) {
                return e.getErrorEval();
            }
            int startIx = startCharNum - 1;



            if (startIx < 0) {
                return ErrorEval.VALUE_INVALID;
            }
            if (numChars < 0) {
                return ErrorEval.VALUE_INVALID;
            }
            int len = text.length();
            if (numChars < 0 || startIx > len) {
                return new StringEval("");
            }
            int endIx = Math.min(startIx + numChars, len);
            String result = text.substring(startIx, endIx);
            return new StringEval(result);
        }
    };
    public static final Function LEFT = new LeftRight(true);
    public static final Function RIGHT = new LeftRight(false);
    public static final Function CONCATENATE = new Function() {

        public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0, iSize = args.length; i < iSize; i++) {
                try {
                    sb.append(evaluateStringArg(args[i], srcRowIndex, srcColumnIndex));
                } catch (EvaluationException e) {
                    return e.getErrorEval();
                }
            }
            return new StringEval(sb.toString());
        }
    };
    public static final Function EXACT = new Fixed2ArgFunction() {

        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0,
                                  ValueEval arg1) {
            String s0;
            String s1;
            try {
                s0 = evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                s1 = evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
            } catch (EvaluationException e) {
                return e.getErrorEval();
            }
            return BoolEval.valueOf(s0.equals(s1));
        }
    };

    public static final Function TEXT = new Fixed2ArgFunction() {

        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
            double s0;
            String s1;
            try {
                s0 = evaluateDoubleArg(arg0, srcRowIndex, srcColumnIndex);
                s1 = evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
            } catch (EvaluationException e) {
                return e.getErrorEval();
            }
            if (s1.matches("[\\d,\\#,\\.,\\$,\\,]+")) {
                NumberFormat formatter = new DecimalFormat(s1);
                return new StringEval(formatter.format(s0));
            } else if (s1.indexOf("/") == s1.lastIndexOf("/") && s1.indexOf("/") >= 0 && !s1.contains("-")) {
                double wholePart = Math.floor(s0);
                double decPart = s0 - wholePart;
                if (wholePart * decPart == 0) {
                    return new StringEval("0");
                }
                String[] parts = s1.split(" ");
                String[] fractParts;
                if (parts.length == 2) {
                    fractParts = parts[1].split("/");
                } else {
                    fractParts = s1.split("/");
                }

                if (fractParts.length == 2) {
                    double minVal = 1.0;
                    double currDenom = Math.pow(10, fractParts[1].length()) - 1d;
                    double currNeum = 0;
                    for (int i = (int) (Math.pow(10, fractParts[1].length()) - 1d); i > 0; i--) {
                        for (int i2 = (int) (Math.pow(10, fractParts[1].length()) - 1d); i2 > 0; i2--) {
                            if (minVal >= Math.abs((double) i2 / (double) i - decPart)) {
                                currDenom = i;
                                currNeum = i2;
                                minVal = Math.abs((double) i2 / (double) i - decPart);
                            }
                        }
                    }
                    NumberFormat neumFormatter = new DecimalFormat(fractParts[0]);
                    NumberFormat denomFormatter = new DecimalFormat(fractParts[1]);
                    if (parts.length == 2) {
                        NumberFormat wholeFormatter = new DecimalFormat(parts[0]);
                        String result = wholeFormatter.format(wholePart) + " " + neumFormatter.format(currNeum) + "/" + denomFormatter.format(currDenom);
                        return new StringEval(result);
                    } else {
                        String result = neumFormatter.format(currNeum + (currDenom * wholePart)) + "/" + denomFormatter.format(currDenom);
                        return new StringEval(result);
                    }
                } else {
                    return ErrorEval.VALUE_INVALID;
                }
            } else {
                try {
                    DateFormat dateFormatter = new SimpleDateFormat(s1);
                    Calendar cal = new GregorianCalendar(1899, 11, 30, 0, 0, 0);
                    cal.add(Calendar.DATE, (int) Math.floor(s0));
                    double dayFraction = s0 - Math.floor(s0);
                    cal.add(Calendar.MILLISECOND, (int) Math.round(dayFraction * 24 * 60 * 60 * 1000));
                    return new StringEval(dateFormatter.format(cal.getTime()));
                } catch (Exception e) {
                    return ErrorEval.VALUE_INVALID;
                }
            }
        }
    };

    public static final Function FIND = new SearchFind(true);

    public static final Function SEARCH = new SearchFind(false);
    protected static final String EMPTY_STRING = "";

    protected static final String evaluateStringArg(ValueEval eval, int srcRow, int srcCol) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(eval, srcRow, srcCol);
        return OperandResolver.coerceValueToString(ve);
    }

    protected static final int evaluateIntArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        return OperandResolver.coerceValueToInt(ve);
    }

    protected static final double evaluateDoubleArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        return OperandResolver.coerceValueToDouble(ve);
    }

    public final ValueEval evaluate(ValueEval[] args, int srcCellRow, int srcCellCol) {
        try {
            return evaluateFunc(args, srcCellRow, srcCellCol);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    protected abstract ValueEval evaluateFunc(ValueEval[] args, int srcCellRow, int srcCellCol) throws EvaluationException;

    private static abstract class SingleArgTextFunc extends Fixed1ArgFunction {

        protected SingleArgTextFunc() {

        }

        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            String arg;
            try {
                arg = evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
            } catch (EvaluationException e) {
                return e.getErrorEval();
            }
            return evaluate(arg);
        }

        protected abstract ValueEval evaluate(String arg);
    }

    private static final class LeftRight extends Var1or2ArgFunction {
        private static final ValueEval DEFAULT_ARG1 = new NumberEval(1.0);
        private final boolean _isLeft;

        protected LeftRight(boolean isLeft) {
            _isLeft = isLeft;
        }

        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            return evaluate(srcRowIndex, srcColumnIndex, arg0, DEFAULT_ARG1);
        }

        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0,
                                  ValueEval arg1) {
            String arg;
            int index;
            try {
                arg = evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                index = evaluateIntArg(arg1, srcRowIndex, srcColumnIndex);
            } catch (EvaluationException e) {
                return e.getErrorEval();
            }

            if (index < 0) {
                return ErrorEval.VALUE_INVALID;
            }

            String result;
            if (_isLeft) {
                result = arg.substring(0, Math.min(arg.length(), index));
            } else {
                result = arg.substring(Math.max(0, arg.length() - index));
            }
            return new StringEval(result);
        }
    }

    private static final class SearchFind extends Var2or3ArgFunction {

        private final boolean _isCaseSensitive;

        public SearchFind(boolean isCaseSensitive) {
            _isCaseSensitive = isCaseSensitive;
        }

        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
            try {
                String needle = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                String haystack = TextFunction.evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
                return eval(haystack, needle, 0);
            } catch (EvaluationException e) {
                return e.getErrorEval();
            }
        }

        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1,
                                  ValueEval arg2) {
            try {
                String needle = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                String haystack = TextFunction.evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);

                int startpos = TextFunction.evaluateIntArg(arg2, srcRowIndex, srcColumnIndex) - 1;
                if (startpos < 0) {
                    return ErrorEval.VALUE_INVALID;
                }
                return eval(haystack, needle, startpos);
            } catch (EvaluationException e) {
                return e.getErrorEval();
            }
        }

        private ValueEval eval(String haystack, String needle, int startIndex) {
            int result;
            if (_isCaseSensitive) {
                result = haystack.indexOf(needle, startIndex);
            } else {
                result = haystack.toUpperCase().indexOf(needle.toUpperCase(), startIndex);
            }
            if (result == -1) {
                return ErrorEval.VALUE_INVALID;
            }
            return new NumberEval(result + 1);
        }
    }
}
