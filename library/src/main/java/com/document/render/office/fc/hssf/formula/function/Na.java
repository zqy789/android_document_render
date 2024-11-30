

package com.document.render.office.fc.hssf.formula.function;

import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;


public final class Na extends Fixed0ArgFunction {

    public ValueEval evaluate(int srcCellRow, int srcCellCol) {
        return ErrorEval.NA;
    }
}
