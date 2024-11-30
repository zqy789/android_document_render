

package com.document.render.office.fc.hssf.formula.function;

import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.ss.util.DateUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;



public final class CalendarFieldFunction extends Fixed1ArgFunction {
    public static final Function YEAR = new CalendarFieldFunction(Calendar.YEAR);
    public static final Function MONTH = new CalendarFieldFunction(Calendar.MONTH);
    public static final Function WEEKDAY = new CalendarFieldFunction(Calendar.DAY_OF_WEEK);
    public static final Function DAY = new CalendarFieldFunction(Calendar.DAY_OF_MONTH);
    public static final Function HOUR = new CalendarFieldFunction(Calendar.HOUR_OF_DAY);
    public static final Function MINUTE = new CalendarFieldFunction(Calendar.MINUTE);
    public static final Function SECOND = new CalendarFieldFunction(Calendar.SECOND);

    private final int _dateFieldId;

    private CalendarFieldFunction(int dateFieldId) {
        _dateFieldId = dateFieldId;
    }

    public final ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        double val;
        try {
            ValueEval ve = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            val = OperandResolver.coerceValueToDouble(ve);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
        if (val < 0) {
            return ErrorEval.NUM_ERROR;
        }
        return new NumberEval(getCalField(val));
    }

    private int getCalField(double serialDate) {


        if (((int) serialDate) == 0) {
            switch (_dateFieldId) {
                case Calendar.YEAR:
                    return 1900;
                case Calendar.MONTH:
                    return 1;
                case Calendar.DAY_OF_MONTH:
                    return 0;
            }

        }


        Date d = DateUtil.getJavaDate(serialDate, false);

        Calendar c = new GregorianCalendar();
        c.setTime(d);
        int result = c.get(_dateFieldId);


        if (_dateFieldId == Calendar.MONTH) {
            result++;
        }

        return result;
    }
}
