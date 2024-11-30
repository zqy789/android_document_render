

package com.document.render.office.fc.hssf.formula.function;

import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.ss.util.DateUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;



public final class Today extends Fixed0ArgFunction {

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex) {

        Calendar now = new GregorianCalendar();
        now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE), 0, 0, 0);
        now.set(Calendar.MILLISECOND, 0);
        return new NumberEval(DateUtil.getExcelDate(now.getTime()));
    }
}
