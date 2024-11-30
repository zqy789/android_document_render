

package com.document.render.office.fc.hssf.formula.function;

import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.ss.util.DateUtil;

import java.util.Date;



public final class Now extends Fixed0ArgFunction {

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex) {
        Date now = new Date(System.currentTimeMillis());
        return new NumberEval(DateUtil.getExcelDate(now));
    }
}
