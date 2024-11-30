

package com.document.render.office.fc.hssf.formula.function;

import com.document.render.office.fc.hssf.formula.eval.BlankEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.MissingArgEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;


public interface Function {


    ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex);
}
