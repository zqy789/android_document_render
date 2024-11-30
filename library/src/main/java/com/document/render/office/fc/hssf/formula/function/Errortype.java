

package com.document.render.office.fc.hssf.formula.function;


import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.fc.ss.usermodel.ErrorConstants;



public final class Errortype extends Fixed1ArgFunction {

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {

        try {
            OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            return ErrorEval.NA;
        } catch (EvaluationException e) {
            int result = translateErrorCodeToErrorTypeValue(e.getErrorEval().getErrorCode());
            return new NumberEval(result);
        }
    }

    private int translateErrorCodeToErrorTypeValue(int errorCode) {
        switch (errorCode) {
            case ErrorConstants.ERROR_NULL:
                return 1;
            case ErrorConstants.ERROR_DIV_0:
                return 2;
            case ErrorConstants.ERROR_VALUE:
                return 3;
            case ErrorConstants.ERROR_REF:
                return 4;
            case ErrorConstants.ERROR_NAME:
                return 5;
            case ErrorConstants.ERROR_NUM:
                return 6;
            case ErrorConstants.ERROR_NA:
                return 7;
        }
        throw new IllegalArgumentException("Invalid error code (" + errorCode + ")");
    }

}
