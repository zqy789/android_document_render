

package com.document.render.office.fc.hssf.formula.function;


import com.document.render.office.fc.hssf.formula.OperationEvaluationContext;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;



public interface FreeRefFunction {

    ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec);
}
