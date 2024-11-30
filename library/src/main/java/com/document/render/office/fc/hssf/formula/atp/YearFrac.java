

package com.document.render.office.fc.hssf.formula.atp;

import com.document.render.office.fc.hssf.formula.OperationEvaluationContext;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.StringEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.fc.hssf.formula.function.FreeRefFunction;
import com.document.render.office.ss.util.DateUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;



final class YearFrac implements FreeRefFunction {

    public static final FreeRefFunction instance = new YearFrac();

    private YearFrac() {

    }

    private static double evaluateDateArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, (short) srcCellCol);

        if (ve instanceof StringEval) {
            String strVal = ((StringEval) ve).getStringValue();
            Double dVal = OperandResolver.parseDouble(strVal);
            if (dVal != null) {
                return dVal.doubleValue();
            }
            Calendar date = parseDate(strVal);
            return DateUtil.getExcelDate(date, false);
        }
        return OperandResolver.coerceValueToDouble(ve);
    }

    private static Calendar parseDate(String strVal) throws EvaluationException {
        String[] parts = Pattern.compile("/").split(strVal);
        if (parts.length != 3) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        String part2 = parts[2];
        int spacePos = part2.indexOf(' ');
        if (spacePos > 0) {

            part2 = part2.substring(0, spacePos);
        }
        int f0;
        int f1;
        int f2;
        try {
            f0 = Integer.parseInt(parts[0]);
            f1 = Integer.parseInt(parts[1]);
            f2 = Integer.parseInt(part2);
        } catch (NumberFormatException e) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        if (f0 < 0 || f1 < 0 || f2 < 0 || (f0 > 12 && f1 > 12 && f2 > 12)) {

            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }

        if (f0 >= 1900 && f0 < 9999) {

            return makeDate(f0, f1, f2);
        }

        if (false) {

            return makeDate(f2, f0, f1);
        }

        throw new RuntimeException("Unable to determine date format for text '" + strVal + "'");
    }


    private static Calendar makeDate(int year, int month, int day) throws EvaluationException {
        if (month < 1 || month > 12) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        Calendar cal = new GregorianCalendar(year, month - 1, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        if (day < 1 || day > cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal;
    }

    private static int evaluateIntArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, (short) srcCellCol);
        return OperandResolver.coerceValueToInt(ve);
    }

    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        int srcCellRow = ec.getRowIndex();
        int srcCellCol = ec.getColumnIndex();
        double result;
        try {
            int basis = 0;
            switch (args.length) {
                case 3:
                    basis = evaluateIntArg(args[2], srcCellRow, srcCellCol);
                case 2:
                    break;
                default:
                    return ErrorEval.VALUE_INVALID;
            }
            double startDateVal = evaluateDateArg(args[0], srcCellRow, srcCellCol);
            double endDateVal = evaluateDateArg(args[1], srcCellRow, srcCellCol);
            result = YearFracCalculator.calculate(startDateVal, endDateVal, basis);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }

        return new NumberEval(result);
    }
}
