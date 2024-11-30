

package com.document.render.office.fc.hssf.formula.function;

import static com.document.render.office.fc.hssf.formula.function.AggregateFunction.subtotalInstance;

import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.NotImplementedException;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;


public class Subtotal implements Function {

    private static Function findFunction(int functionCode) throws EvaluationException {
        Function func;
        switch (functionCode) {
            case 1:
                return subtotalInstance(AggregateFunction.AVERAGE);
            case 2:
                return Count.subtotalInstance();
            case 3:
                return Counta.subtotalInstance();
            case 4:
                return subtotalInstance(AggregateFunction.MAX);
            case 5:
                return subtotalInstance(AggregateFunction.MIN);
            case 6:
                return subtotalInstance(AggregateFunction.PRODUCT);
            case 7:
                return subtotalInstance(AggregateFunction.STDEV);
            case 8:
                throw new NotImplementedException("STDEVP");
            case 9:
                return subtotalInstance(AggregateFunction.SUM);
            case 10:
                throw new NotImplementedException("VAR");
            case 11:
                throw new NotImplementedException("VARP");
        }
        if (functionCode > 100 && functionCode < 112) {
            throw new NotImplementedException("SUBTOTAL - with 'exclude hidden values' option");
        }
        throw EvaluationException.invalidValue();
    }

    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        int nInnerArgs = args.length - 1;
        if (nInnerArgs < 1) {
            return ErrorEval.VALUE_INVALID;
        }

        Function innerFunc;
        try {
            ValueEval ve = OperandResolver.getSingleValue(args[0], srcRowIndex, srcColumnIndex);
            int functionCode = OperandResolver.coerceValueToInt(ve);
            innerFunc = findFunction(functionCode);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }

        ValueEval[] innerArgs = new ValueEval[nInnerArgs];
        System.arraycopy(args, 1, innerArgs, 0, nInnerArgs);

        return innerFunc.evaluate(innerArgs, srcRowIndex, srcColumnIndex);
    }
}
