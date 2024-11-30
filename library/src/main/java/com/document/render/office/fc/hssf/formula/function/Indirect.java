

package com.document.render.office.fc.hssf.formula.function;


import com.document.render.office.fc.hssf.formula.OperationEvaluationContext;
import com.document.render.office.fc.hssf.formula.eval.BlankEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.MissingArgEval;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;



public final class Indirect implements FreeRefFunction {

    public static final FreeRefFunction instance = new Indirect();

    private Indirect() {

    }

    private static boolean evaluateBooleanArg(ValueEval arg, OperationEvaluationContext ec)
            throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(arg, ec.getRowIndex(), ec.getColumnIndex());

        if (ve == BlankEval.instance || ve == MissingArgEval.instance) {
            return false;
        }


        return OperandResolver.coerceValueToBoolean(ve, false).booleanValue();
    }

    private static ValueEval evaluateIndirect(OperationEvaluationContext ec, String text,
                                              boolean isA1style) {

        int plingPos = text.lastIndexOf('!');

        String workbookName;
        String sheetName;
        String refText;
        if (plingPos < 0) {
            workbookName = null;
            sheetName = null;
            refText = text;
        } else {
            String[] parts = parseWorkbookAndSheetName(text.subSequence(0, plingPos));
            if (parts == null) {
                return ErrorEval.REF_INVALID;
            }
            workbookName = parts[0];
            sheetName = parts[1];
            refText = text.substring(plingPos + 1);
        }

        String refStrPart1;
        String refStrPart2;

        int colonPos = refText.indexOf(':');
        if (colonPos < 0) {
            refStrPart1 = refText.trim();
            refStrPart2 = null;
        } else {
            refStrPart1 = refText.substring(0, colonPos).trim();
            refStrPart2 = refText.substring(colonPos + 1).trim();
        }
        return ec.getDynamicReference(workbookName, sheetName, refStrPart1, refStrPart2, isA1style);
    }


    private static String[] parseWorkbookAndSheetName(CharSequence text) {
        int lastIx = text.length() - 1;
        if (lastIx < 0) {
            return null;
        }
        if (canTrim(text)) {
            return null;
        }
        char firstChar = text.charAt(0);
        if (Character.isWhitespace(firstChar)) {
            return null;
        }
        if (firstChar == '\'') {


            if (text.charAt(lastIx) != '\'') {
                return null;
            }
            firstChar = text.charAt(1);
            if (Character.isWhitespace(firstChar)) {
                return null;
            }
            String wbName;
            int sheetStartPos;
            if (firstChar == '[') {
                int rbPos = text.toString().lastIndexOf(']');
                if (rbPos < 0) {
                    return null;
                }
                wbName = unescapeString(text.subSequence(2, rbPos));
                if (wbName == null || canTrim(wbName)) {
                    return null;
                }
                sheetStartPos = rbPos + 1;
            } else {
                wbName = null;
                sheetStartPos = 1;
            }


            String sheetName = unescapeString(text.subSequence(sheetStartPos, lastIx));
            if (sheetName == null) {

                return null;
            }
            return new String[]{wbName, sheetName,};
        }

        if (firstChar == '[') {
            int rbPos = text.toString().lastIndexOf(']');
            if (rbPos < 0) {
                return null;
            }
            CharSequence wbName = text.subSequence(1, rbPos);
            if (canTrim(wbName)) {
                return null;
            }
            CharSequence sheetName = text.subSequence(rbPos + 1, text.length());
            if (canTrim(sheetName)) {
                return null;
            }
            return new String[]{wbName.toString(), sheetName.toString(),};
        }

        return new String[]{null, text.toString(),};
    }


    private static String unescapeString(CharSequence text) {
        int len = text.length();
        StringBuilder sb = new StringBuilder(len);
        int i = 0;
        while (i < len) {
            char ch = text.charAt(i);
            if (ch == '\'') {

                i++;
                if (i >= len) {
                    return null;
                }
                ch = text.charAt(i);
                if (ch != '\'') {
                    return null;
                }
            }
            sb.append(ch);
            i++;
        }
        return sb.toString();
    }

    private static boolean canTrim(CharSequence text) {
        int lastIx = text.length() - 1;
        if (lastIx < 0) {
            return false;
        }
        if (Character.isWhitespace(text.charAt(0))) {
            return true;
        }
        if (Character.isWhitespace(text.charAt(lastIx))) {
            return true;
        }
        return false;
    }

    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length < 1) {
            return ErrorEval.VALUE_INVALID;
        }

        boolean isA1style;
        String text;
        try {
            ValueEval ve = OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec
                    .getColumnIndex());
            text = OperandResolver.coerceValueToString(ve);
            switch (args.length) {
                case 1:
                    isA1style = true;
                    break;
                case 2:
                    isA1style = evaluateBooleanArg(args[1], ec);
                    break;
                default:
                    return ErrorEval.VALUE_INVALID;
            }
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }

        return evaluateIndirect(ec, text, isA1style);
    }
}
