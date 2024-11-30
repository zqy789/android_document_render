

package com.document.render.office.fc.hssf.formula;

import com.document.render.office.fc.hssf.formula.ptg.AbstractFunctionPtg;
import com.document.render.office.fc.hssf.formula.ptg.AttrPtg;
import com.document.render.office.fc.hssf.formula.ptg.ControlPtg;
import com.document.render.office.fc.hssf.formula.ptg.FuncVarPtg;
import com.document.render.office.fc.hssf.formula.ptg.MemAreaPtg;
import com.document.render.office.fc.hssf.formula.ptg.MemFuncPtg;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.formula.ptg.RangePtg;
import com.document.render.office.fc.hssf.formula.ptg.UnionPtg;
import com.document.render.office.fc.hssf.formula.ptg.ValueOperatorPtg;


final class OperandClassTransformer {

    private final int _formulaType;

    public OperandClassTransformer(int formulaType) {
        _formulaType = formulaType;
    }

    private static boolean isSingleArgSum(Ptg token) {
        if (token instanceof AttrPtg) {
            AttrPtg attrPtg = (AttrPtg) token;
            return attrPtg.isSum();
        }
        return false;
    }

    private static boolean isSimpleValueFunction(Ptg token) {
        if (token instanceof AbstractFunctionPtg) {
            AbstractFunctionPtg aptg = (AbstractFunctionPtg) token;
            if (aptg.getDefaultOperandClass() != Ptg.CLASS_VALUE) {
                return false;
            }
            int numberOfOperands = aptg.getNumberOfOperands();
            for (int i = numberOfOperands - 1; i >= 0; i--) {
                if (aptg.getParameterClass(i) != Ptg.CLASS_VALUE) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    public void transformFormula(ParseNode rootNode) {
        byte rootNodeOperandClass;
        switch (_formulaType) {
            case FormulaType.CELL:
                rootNodeOperandClass = Ptg.CLASS_VALUE;
                break;
            case FormulaType.ARRAY:
                rootNodeOperandClass = Ptg.CLASS_ARRAY;
                break;
            case FormulaType.NAMEDRANGE:
            case FormulaType.DATAVALIDATION_LIST:
                rootNodeOperandClass = Ptg.CLASS_REF;
                break;
            default:
                throw new RuntimeException("Incomplete code - formula type ("
                        + _formulaType + ") not supported yet");

        }
        transformNode(rootNode, rootNodeOperandClass, false);
    }


    private void transformNode(ParseNode node, byte desiredOperandClass,
                               boolean callerForceArrayFlag) {
        Ptg token = node.getToken();
        ParseNode[] children = node.getChildren();
        boolean isSimpleValueFunc = isSimpleValueFunction(token);

        if (isSimpleValueFunc) {
            boolean localForceArray = desiredOperandClass == Ptg.CLASS_ARRAY;
            for (int i = 0; i < children.length; i++) {
                transformNode(children[i], desiredOperandClass, localForceArray);
            }
            setSimpleValueFuncClass((AbstractFunctionPtg) token, desiredOperandClass, callerForceArrayFlag);
            return;
        }

        if (isSingleArgSum(token)) {


            token = FuncVarPtg.SUM;


        }
        if (token instanceof ValueOperatorPtg || token instanceof ControlPtg
                || token instanceof MemFuncPtg
                || token instanceof MemAreaPtg
                || token instanceof UnionPtg) {






            byte localDesiredOperandClass = desiredOperandClass == Ptg.CLASS_REF ? Ptg.CLASS_VALUE : desiredOperandClass;
            for (int i = 0; i < children.length; i++) {
                transformNode(children[i], localDesiredOperandClass, callerForceArrayFlag);
            }
            return;
        }
        if (token instanceof AbstractFunctionPtg) {
            transformFunctionNode((AbstractFunctionPtg) token, children, desiredOperandClass, callerForceArrayFlag);
            return;
        }
        if (children.length > 0) {
            if (token == RangePtg.instance) {

                return;
            }
            throw new IllegalStateException("Node should not have any children");
        }

        if (token.isBaseToken()) {

            return;
        }
        token.setClass(transformClass(token.getPtgClass(), desiredOperandClass, callerForceArrayFlag));
    }

    private byte transformClass(byte currentOperandClass, byte desiredOperandClass,
                                boolean callerForceArrayFlag) {
        switch (desiredOperandClass) {
            case Ptg.CLASS_VALUE:
                if (!callerForceArrayFlag) {
                    return Ptg.CLASS_VALUE;
                }

            case Ptg.CLASS_ARRAY:
                return Ptg.CLASS_ARRAY;
            case Ptg.CLASS_REF:
                if (!callerForceArrayFlag) {
                    return currentOperandClass;
                }
                return Ptg.CLASS_REF;
        }
        throw new IllegalStateException("Unexpected operand class (" + desiredOperandClass + ")");
    }

    private void transformFunctionNode(AbstractFunctionPtg afp, ParseNode[] children,
                                       byte desiredOperandClass, boolean callerForceArrayFlag) {

        boolean localForceArrayFlag;
        byte defaultReturnOperandClass = afp.getDefaultOperandClass();

        if (callerForceArrayFlag) {
            switch (defaultReturnOperandClass) {
                case Ptg.CLASS_REF:
                    if (desiredOperandClass == Ptg.CLASS_REF) {
                        afp.setClass(Ptg.CLASS_REF);
                    } else {
                        afp.setClass(Ptg.CLASS_ARRAY);
                    }
                    localForceArrayFlag = false;
                    break;
                case Ptg.CLASS_ARRAY:
                    afp.setClass(Ptg.CLASS_ARRAY);
                    localForceArrayFlag = false;
                    break;
                case Ptg.CLASS_VALUE:
                    afp.setClass(Ptg.CLASS_ARRAY);
                    localForceArrayFlag = true;
                    break;
                default:
                    throw new IllegalStateException("Unexpected operand class ("
                            + defaultReturnOperandClass + ")");
            }
        } else {
            if (defaultReturnOperandClass == desiredOperandClass) {
                localForceArrayFlag = false;



                afp.setClass(defaultReturnOperandClass);
            } else {
                switch (desiredOperandClass) {
                    case Ptg.CLASS_VALUE:

                        afp.setClass(Ptg.CLASS_VALUE);
                        localForceArrayFlag = false;
                        break;
                    case Ptg.CLASS_ARRAY:
                        switch (defaultReturnOperandClass) {
                            case Ptg.CLASS_REF:
                                afp.setClass(Ptg.CLASS_REF);

                                break;
                            case Ptg.CLASS_VALUE:
                                afp.setClass(Ptg.CLASS_ARRAY);
                                break;
                            default:
                                throw new IllegalStateException("Unexpected operand class ("
                                        + defaultReturnOperandClass + ")");
                        }
                        localForceArrayFlag = (defaultReturnOperandClass == Ptg.CLASS_VALUE);
                        break;
                    case Ptg.CLASS_REF:
                        switch (defaultReturnOperandClass) {
                            case Ptg.CLASS_ARRAY:
                                afp.setClass(Ptg.CLASS_ARRAY);
                                break;
                            case Ptg.CLASS_VALUE:
                                afp.setClass(Ptg.CLASS_VALUE);
                                break;
                            default:
                                throw new IllegalStateException("Unexpected operand class ("
                                        + defaultReturnOperandClass + ")");
                        }
                        localForceArrayFlag = false;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected operand class ("
                                + desiredOperandClass + ")");
                }

            }
        }

        for (int i = 0; i < children.length; i++) {
            ParseNode child = children[i];
            byte paramOperandClass = afp.getParameterClass(i);
            transformNode(child, paramOperandClass, localForceArrayFlag);
        }
    }

    private void setSimpleValueFuncClass(AbstractFunctionPtg afp,
                                         byte desiredOperandClass, boolean callerForceArrayFlag) {

        if (callerForceArrayFlag || desiredOperandClass == Ptg.CLASS_ARRAY) {
            afp.setClass(Ptg.CLASS_ARRAY);
        } else {
            afp.setClass(Ptg.CLASS_VALUE);
        }
    }
}
