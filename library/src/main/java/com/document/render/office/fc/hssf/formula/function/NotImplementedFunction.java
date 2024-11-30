

package com.document.render.office.fc.hssf.formula.function;

import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.NotImplementedException;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.fc.ss.usermodel.ErrorConstants;


public final class NotImplementedFunction implements Function {
    private final String _functionName;

    protected NotImplementedFunction() {
        _functionName = getClass().getName();
    }

    public NotImplementedFunction(String name) {
        _functionName = name;
    }

    public ValueEval evaluate(ValueEval[] operands, int srcRow, int srcCol) {

        return ErrorEval.valueOf(ErrorConstants.ERROR_NAME);

    }

    public String getFunctionName() {
        return _functionName;
    }
}
