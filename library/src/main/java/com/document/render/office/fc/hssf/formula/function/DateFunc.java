

package com.document.render.office.fc.hssf.formula.function;

import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.ss.util.DateUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;



public final class DateFunc extends Fixed3ArgFunction {

    public static final Function instance = new DateFunc();

    private DateFunc() {

    }

    private static double evaluate(int year, int month, int pDay) throws EvaluationException {

        if (year < 0 || month < 0 || pDay < 0) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }

        if (year == 1900 && month == Calendar.FEBRUARY && pDay == 29) {
            return 60.0;
        }

        int day = pDay;
        if (year == 1900) {
            if ((month == Calendar.JANUARY && day >= 60) ||
                    (month == Calendar.FEBRUARY && day >= 30)) {
                day--;
            }
        }

        Calendar c = new GregorianCalendar();

        c.set(year, month, day, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);

        return DateUtil.getExcelDate(c.getTime(), false);
    }

    private static int getYear(double d) {
        int year = (int) d;

        if (year < 0) {
            return -1;
        }

        return year < 1900 ? 1900 + year : year;
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1,
                              ValueEval arg2) {
        double result;
        try {
            double d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
            double d1 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
            double d2 = NumericFunction.singleOperandEvaluate(arg2, srcRowIndex, srcColumnIndex);
            result = evaluate(getYear(d0), (int) (d1 - 1), (int) d2);
            NumericFunction.checkValue(result);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(result);
    }
}
