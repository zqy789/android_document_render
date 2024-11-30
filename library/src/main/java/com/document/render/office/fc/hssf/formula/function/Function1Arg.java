

package com.document.render.office.fc.hssf.formula.function;

import com.document.render.office.fc.hssf.formula.eval.ValueEval;


public interface Function1Arg extends Function {

    ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0);
}
