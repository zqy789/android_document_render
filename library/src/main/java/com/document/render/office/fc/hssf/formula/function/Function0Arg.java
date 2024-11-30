

package com.document.render.office.fc.hssf.formula.function;

import com.document.render.office.fc.hssf.formula.eval.ValueEval;


public interface Function0Arg extends Function {

    ValueEval evaluate(int srcRowIndex, int srcColumnIndex);
}
