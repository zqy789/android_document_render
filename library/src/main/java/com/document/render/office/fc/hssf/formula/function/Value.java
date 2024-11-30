

package com.document.render.office.fc.hssf.formula.function;

import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;


public final class Value extends Fixed1ArgFunction {


    private static final int MIN_DISTANCE_BETWEEN_THOUSANDS_SEPARATOR = 4;
    private static final Double ZERO = new Double(0.0);


    private static Double convertTextToNumber(String strText) {
        boolean foundCurrency = false;
        boolean foundUnaryPlus = false;
        boolean foundUnaryMinus = false;

        int len = strText.length();
        int i;
        for (i = 0; i < len; i++) {
            char ch = strText.charAt(i);
            if (Character.isDigit(ch) || ch == '.') {
                break;
            }
            switch (ch) {
                case ' ':

                    continue;
                case '$':
                    if (foundCurrency) {

                        return null;
                    }
                    foundCurrency = true;
                    continue;
                case '+':
                    if (foundUnaryMinus || foundUnaryPlus) {
                        return null;
                    }
                    foundUnaryPlus = true;
                    continue;
                case '-':
                    if (foundUnaryMinus || foundUnaryPlus) {
                        return null;
                    }
                    foundUnaryMinus = true;
                    continue;
                default:

                    return null;
            }
        }
        if (i >= len) {

            if (foundCurrency || foundUnaryMinus || foundUnaryPlus) {
                return null;
            }
            return ZERO;
        }



        boolean foundDecimalPoint = false;
        int lastThousandsSeparatorIndex = Short.MIN_VALUE;

        StringBuffer sb = new StringBuffer(len);
        for (; i < len; i++) {
            char ch = strText.charAt(i);
            if (Character.isDigit(ch)) {
                sb.append(ch);
                continue;
            }
            switch (ch) {
                case ' ':
                    String remainingText = strText.substring(i);
                    if (remainingText.trim().length() > 0) {

                        return null;
                    }
                    break;
                case '.':
                    if (foundDecimalPoint) {
                        return null;
                    }
                    if (i - lastThousandsSeparatorIndex < MIN_DISTANCE_BETWEEN_THOUSANDS_SEPARATOR) {
                        return null;
                    }
                    foundDecimalPoint = true;
                    sb.append('.');
                    continue;
                case ',':
                    if (foundDecimalPoint) {

                        return null;
                    }
                    int distanceBetweenThousandsSeparators = i - lastThousandsSeparatorIndex;

                    if (distanceBetweenThousandsSeparators < MIN_DISTANCE_BETWEEN_THOUSANDS_SEPARATOR) {
                        return null;
                    }
                    lastThousandsSeparatorIndex = i;

                    continue;

                case 'E':
                case 'e':
                    if (i - lastThousandsSeparatorIndex < MIN_DISTANCE_BETWEEN_THOUSANDS_SEPARATOR) {
                        return null;
                    }

                    sb.append(strText.substring(i));
                    i = len;
                    break;
                default:

                    return null;
            }
        }
        if (!foundDecimalPoint) {
            if (i - lastThousandsSeparatorIndex < MIN_DISTANCE_BETWEEN_THOUSANDS_SEPARATOR) {
                return null;
            }
        }
        double d;
        try {
            d = Double.parseDouble(sb.toString());
        } catch (NumberFormatException e) {

            return null;
        }
        return new Double(foundUnaryMinus ? -d : d);
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        ValueEval veText;
        try {
            veText = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
        String strText = OperandResolver.coerceValueToString(veText);
        Double result = convertTextToNumber(strText);
        if (result == null) {
            return ErrorEval.VALUE_INVALID;
        }
        return new NumberEval(result.doubleValue());
    }
}
