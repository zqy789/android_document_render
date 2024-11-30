

package com.document.render.office.fc.hssf.formula;

import com.document.render.office.fc.hssf.formula.ptg.AttrPtg;
import com.document.render.office.fc.hssf.formula.ptg.MemAreaPtg;
import com.document.render.office.fc.hssf.formula.ptg.MemErrPtg;
import com.document.render.office.fc.hssf.formula.ptg.MemFuncPtg;
import com.document.render.office.fc.hssf.formula.ptg.OperationPtg;
import com.document.render.office.fc.hssf.formula.ptg.ParenthesisPtg;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;

import java.util.Stack;



public class FormulaRenderer {


    public static String toFormulaString(FormulaRenderingWorkbook book, Ptg[] ptgs) {
        if (ptgs == null || ptgs.length == 0) {
            throw new IllegalArgumentException("ptgs must not be null");
        }
        Stack<String> stack = new Stack<String>();

        for (int i = 0; i < ptgs.length; i++) {
            Ptg ptg = ptgs[i];

            if (ptg instanceof MemAreaPtg || ptg instanceof MemFuncPtg || ptg instanceof MemErrPtg) {



                continue;
            }
            if (ptg instanceof ParenthesisPtg) {
                String contents = stack.pop();
                stack.push("(" + contents + ")");
                continue;
            }
            if (ptg instanceof AttrPtg) {
                AttrPtg attrPtg = ((AttrPtg) ptg);
                if (attrPtg.isOptimizedIf() || attrPtg.isOptimizedChoose() || attrPtg.isSkip()) {
                    continue;
                }
                if (attrPtg.isSpace()) {

                    continue;



                }
                if (attrPtg.isSemiVolatile()) {

                    continue;
                }
                if (attrPtg.isSum()) {
                    String[] operands = getOperands(stack, attrPtg.getNumberOfOperands());
                    stack.push(attrPtg.toFormulaString(operands));
                    continue;
                }
                throw new RuntimeException("Unexpected tAttr: " + attrPtg.toString());
            }

            if (ptg instanceof WorkbookDependentFormula) {
                WorkbookDependentFormula optg = (WorkbookDependentFormula) ptg;
                stack.push(optg.toFormulaString(book));
                continue;
            }
            if (!(ptg instanceof OperationPtg)) {
                stack.push(ptg.toFormulaString());
                continue;
            }

            OperationPtg o = (OperationPtg) ptg;
            String[] operands = getOperands(stack, o.getNumberOfOperands());
            stack.push(o.toFormulaString(operands));
        }
        if (stack.isEmpty()) {


            throw new IllegalStateException("Stack underflow");
        }
        String result = stack.pop();
        if (!stack.isEmpty()) {


            throw new IllegalStateException("too much stuff left on the stack");
        }
        return result;
    }

    private static String[] getOperands(Stack<String> stack, int nOperands) {
        String[] operands = new String[nOperands];

        for (int j = nOperands - 1; j >= 0; j--) {
            if (stack.isEmpty()) {
                String msg = "Too few arguments supplied to operation. Expected (" + nOperands
                        + ") operands but got (" + (nOperands - j - 1) + ")";
                throw new IllegalStateException(msg);
            }
            operands[j] = stack.pop();
        }
        return operands;
    }
}
