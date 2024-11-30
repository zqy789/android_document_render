

package com.document.render.office.fc.hssf.formula.function;

import com.document.render.office.fc.hssf.formula.eval.AreaEval;
import com.document.render.office.fc.hssf.formula.eval.BlankEval;
import com.document.render.office.fc.hssf.formula.eval.BoolEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.RefEval;
import com.document.render.office.fc.hssf.formula.eval.StringEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;


public abstract class LogicalFunction extends Fixed1ArgFunction {

    public static final Function ISLOGICAL = new LogicalFunction() {
        protected boolean evaluate(ValueEval arg) {
            return arg instanceof BoolEval;
        }
    };
    public static final Function ISNONTEXT = new LogicalFunction() {
        protected boolean evaluate(ValueEval arg) {
            return !(arg instanceof StringEval);
        }
    };
    public static final Function ISNUMBER = new LogicalFunction() {
        protected boolean evaluate(ValueEval arg) {
            return arg instanceof NumberEval;
        }
    };
    public static final Function ISTEXT = new LogicalFunction() {
        protected boolean evaluate(ValueEval arg) {
            return arg instanceof StringEval;
        }
    };
    public static final Function ISBLANK = new LogicalFunction() {

        protected boolean evaluate(ValueEval arg) {
            return arg instanceof BlankEval;
        }
    };
    public static final Function ISERROR = new LogicalFunction() {

        protected boolean evaluate(ValueEval arg) {
            return arg instanceof ErrorEval;
        }
    };

    public static final Function ISNA = new LogicalFunction() {

        protected boolean evaluate(ValueEval arg) {
            return arg == ErrorEval.NA;
        }
    };
    public static final Function ISREF = new Fixed1ArgFunction() {

        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            if (arg0 instanceof RefEval || arg0 instanceof AreaEval) {
                return BoolEval.TRUE;
            }
            return BoolEval.FALSE;
        }
    };

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        ValueEval ve;
        try {
            ve = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
        } catch (EvaluationException e) {
            if (false) {

                return e.getErrorEval();

            }

            ve = e.getErrorEval();
        }
        return BoolEval.valueOf(evaluate(ve));

    }


    protected abstract boolean evaluate(ValueEval arg);
}
