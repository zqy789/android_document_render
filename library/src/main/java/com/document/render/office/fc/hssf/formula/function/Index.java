

package com.document.render.office.fc.hssf.formula.function;


import com.document.render.office.fc.hssf.formula.TwoDEval;
import com.document.render.office.fc.hssf.formula.eval.BlankEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.MissingArgEval;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.RefEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;



public final class Index implements Function2Arg, Function3Arg, Function4Arg {

    private static TwoDEval convertFirstArg(ValueEval arg0) {
        ValueEval firstArg = arg0;
        if (firstArg instanceof RefEval) {

            return ((RefEval) firstArg).offset(0, 0, 0, 0);
        }
        if ((firstArg instanceof TwoDEval)) {
            return (TwoDEval) firstArg;
        }


        throw new RuntimeException("Incomplete code - cannot handle first arg of type ("
                + firstArg.getClass().getName() + ")");

    }

    private static ValueEval getValueFromArea(TwoDEval ae, int pRowIx, int pColumnIx)
            throws EvaluationException {
        assert pRowIx >= 0;
        assert pColumnIx >= 0;

        TwoDEval result = ae;

        if (pRowIx != 0) {

            if (pRowIx > ae.getHeight()) {

                throw new EvaluationException(ErrorEval.REF_INVALID);
            }
            result = result.getRow(pRowIx - 1);
        }

        if (pColumnIx != 0) {

            if (pColumnIx > ae.getWidth()) {

                throw new EvaluationException(ErrorEval.REF_INVALID);
            }
            result = result.getColumn(pColumnIx - 1);
        }
        return result;
    }


    private static int resolveIndexArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {

        ValueEval ev = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        if (ev == MissingArgEval.instance) {
            return 0;
        }
        if (ev == BlankEval.instance) {
            return 0;
        }
        int result = OperandResolver.coerceValueToInt(ev);
        if (result < 0) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        return result;
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        TwoDEval reference = convertFirstArg(arg0);

        int columnIx = 0;
        try {
            int rowIx = resolveIndexArg(arg1, srcRowIndex, srcColumnIndex);

            if (!reference.isColumn()) {
                if (!reference.isRow()) {


                    return ErrorEval.REF_INVALID;
                }


                columnIx = rowIx;
                rowIx = 0;
            }

            return getValueFromArea(reference, rowIx, columnIx);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1,
                              ValueEval arg2) {
        TwoDEval reference = convertFirstArg(arg0);

        try {
            int columnIx = resolveIndexArg(arg2, srcRowIndex, srcColumnIndex);
            int rowIx = resolveIndexArg(arg1, srcRowIndex, srcColumnIndex);
            return getValueFromArea(reference, rowIx, columnIx);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1,
                              ValueEval arg2, ValueEval arg3) {
        throw new RuntimeException("Incomplete code"
                + " - don't know how to support the 'area_num' parameter yet)");




    }

    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        switch (args.length) {
            case 2:
                return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1]);
            case 3:
                return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2]);
            case 4:
                return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2], args[3]);
        }
        return ErrorEval.VALUE_INVALID;
    }
}
