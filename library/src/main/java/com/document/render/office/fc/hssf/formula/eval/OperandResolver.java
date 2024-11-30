

package com.document.render.office.fc.hssf.formula.eval;

import java.util.regex.Pattern;


public final class OperandResolver {



    private static final String Digits = "(\\p{Digit}+)";
    private static final String Exp = "[eE][+-]?" + Digits;
    private static final String fpRegex =
            ("[\\x00-\\x20]*" +
                    "[+-]?(" +
                    "(((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|" +
                    "(\\.(" + Digits + ")(" + Exp + ")?))))" +
                    "[\\x00-\\x20]*");


    private OperandResolver() {

    }


    public static ValueEval getSingleValue(ValueEval arg, int srcCellRow, int srcCellCol)
            throws EvaluationException {
        ValueEval result;
        if (arg instanceof RefEval) {
            result = ((RefEval) arg).getInnerValueEval();
        } else if (arg instanceof AreaEval) {
            result = chooseSingleElementFromArea((AreaEval) arg, srcCellRow, srcCellCol);
        } else {
            result = arg;
        }
        if (result instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval) result);
        }


        if (result instanceof RefEval) {
            return getSingleValue(result, srcCellRow, srcCellCol);
        }

        return result;
    }


    public static ValueEval chooseSingleElementFromArea(AreaEval ae,
                                                        int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval result = chooseSingleElementFromAreaInternal(ae, srcCellRow, srcCellCol);
        if (result instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval) result);
        }
        return result;
    }


    private static ValueEval chooseSingleElementFromAreaInternal(AreaEval ae,
                                                                 int srcCellRow, int srcCellCol) throws EvaluationException {

        if (false) {

            if (ae.containsRow(srcCellRow) && ae.containsColumn(srcCellCol)) {
                throw new EvaluationException(ErrorEval.CIRCULAR_REF_ERROR);
            }

        }

        if (ae.isColumn()) {
            if (ae.isRow()) {
                return ae.getRelativeValue(0, 0);
            }
            if (!ae.containsRow(srcCellRow)) {
                throw EvaluationException.invalidValue();
            }
            return ae.getAbsoluteValue(srcCellRow, ae.getFirstColumn());
        }
        if (!ae.isRow()) {

            if (ae.containsRow(srcCellRow) && ae.containsColumn(srcCellCol)) {
                return ae.getAbsoluteValue(ae.getFirstRow(), ae.getFirstColumn());
            }
            throw EvaluationException.invalidValue();
        }
        if (!ae.containsColumn(srcCellCol)) {
            throw EvaluationException.invalidValue();
        }
        return ae.getAbsoluteValue(ae.getFirstRow(), srcCellCol);
    }


    public static int coerceValueToInt(ValueEval ev) throws EvaluationException {
        if (ev == BlankEval.instance) {
            return 0;
        }
        double d = coerceValueToDouble(ev);


        return (int) Math.floor(d);
    }


    public static double coerceValueToDouble(ValueEval ev) throws EvaluationException {

        if (ev == BlankEval.instance) {
            return 0.0;
        }
        if (ev instanceof NumericValueEval) {

            return ((NumericValueEval) ev).getNumberValue();
        }
        if (ev instanceof StringEval) {
            Double dd = parseDouble(((StringEval) ev).getStringValue());
            if (dd == null) {
                throw EvaluationException.invalidValue();
            }
            return dd.doubleValue();
        }
        throw new RuntimeException("Unexpected arg eval type (" + ev.getClass().getName() + ")");
    }


    public static Double parseDouble(String pText) {

        if (Pattern.matches(fpRegex, pText))
            try {
                return Double.parseDouble(pText);
            } catch (NumberFormatException e) {
                return null;
            }
        else {
            return null;
        }

    }


    public static String coerceValueToString(ValueEval ve) {
        if (ve instanceof StringValueEval) {
            StringValueEval sve = (StringValueEval) ve;
            return sve.getStringValue();
        }
        if (ve == BlankEval.instance) {
            return "";
        }
        throw new IllegalArgumentException("Unexpected eval class (" + ve.getClass().getName() + ")");
    }


    public static Boolean coerceValueToBoolean(ValueEval ve, boolean stringsAreBlanks) throws EvaluationException {

        if (ve == null || ve == BlankEval.instance) {

            return null;
        }
        if (ve instanceof BoolEval) {
            return Boolean.valueOf(((BoolEval) ve).getBooleanValue());
        }

        if (ve == BlankEval.instance) {
            return null;
        }

        if (ve instanceof StringEval) {
            if (stringsAreBlanks) {
                return null;
            }
            String str = ((StringEval) ve).getStringValue();
            if (str.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            }
            if (str.equalsIgnoreCase("false")) {
                return Boolean.FALSE;
            }

            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }

        if (ve instanceof NumericValueEval) {
            NumericValueEval ne = (NumericValueEval) ve;
            double d = ne.getNumberValue();
            if (Double.isNaN(d)) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            return Boolean.valueOf(d != 0);
        }
        if (ve instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval) ve);
        }
        throw new RuntimeException("Unexpected eval (" + ve.getClass().getName() + ")");
    }
}
