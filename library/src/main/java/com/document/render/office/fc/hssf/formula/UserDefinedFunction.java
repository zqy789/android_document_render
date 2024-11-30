

package com.document.render.office.fc.hssf.formula;


import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.NameEval;
import com.document.render.office.fc.hssf.formula.eval.NameXEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.fc.hssf.formula.function.FreeRefFunction;
import com.document.render.office.fc.ss.usermodel.ErrorConstants;


final class UserDefinedFunction implements FreeRefFunction {

    public static final FreeRefFunction instance = new UserDefinedFunction();

    private UserDefinedFunction() {

    }

    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        int nIncomingArgs = args.length;
        if (nIncomingArgs < 1) {
            throw new RuntimeException("function name argument missing");
        }

        ValueEval nameArg = args[0];
        String functionName;
        if (nameArg instanceof NameEval) {
            functionName = ((NameEval) nameArg).getFunctionName();
        } else if (nameArg instanceof NameXEval) {
            functionName = ec.getWorkbook().resolveNameXText(((NameXEval) nameArg).getPtg());
        } else {
            throw new RuntimeException("First argument should be a NameEval, but got ("
                    + nameArg.getClass().getName() + ")");
        }
        FreeRefFunction targetFunc = ec.findUserDefinedFunction(functionName);
        if (targetFunc == null) {
            return ErrorEval.valueOf(ErrorConstants.ERROR_NAME);

        }
        int nOutGoingArgs = nIncomingArgs - 1;
        ValueEval[] outGoingArgs = new ValueEval[nOutGoingArgs];
        System.arraycopy(args, 1, outGoingArgs, 0, nOutGoingArgs);
        return targetFunc.evaluate(outGoingArgs, ec);
    }
}
