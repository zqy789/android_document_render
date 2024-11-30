

package com.document.render.office.fc.hssf.formula.ptg;

import com.document.render.office.fc.hssf.formula.function.FunctionMetadata;
import com.document.render.office.fc.hssf.formula.function.FunctionMetadataRegistry;



public abstract class AbstractFunctionPtg extends OperationPtg {


    public static final String FUNCTION_NAME_IF = "IF";

    private static final short FUNCTION_INDEX_EXTERNAL = 255;

    private final byte returnClass;
    private final byte[] paramClass;

    private final byte _numberOfArgs;
    private final short _functionIndex;

    protected AbstractFunctionPtg(int functionIndex, int pReturnClass, byte[] paramTypes, int nParams) {
        _numberOfArgs = (byte) nParams;
        _functionIndex = (short) functionIndex;
        returnClass = (byte) pReturnClass;
        paramClass = paramTypes;
    }

    private static void appendArgs(StringBuilder buf, int firstArgIx, String[] operands) {
        buf.append('(');
        for (int i = firstArgIx; i < operands.length; i++) {
            if (i > firstArgIx) {
                buf.append(',');
            }
            buf.append(operands[i]);
        }
        buf.append(")");
    }


    public static final boolean isBuiltInFunctionName(String name) {
        short ix = FunctionMetadataRegistry.lookupIndexByName(name.toUpperCase());
        return ix >= 0;
    }


    protected static short lookupIndex(String name) {
        short ix = FunctionMetadataRegistry.lookupIndexByName(name.toUpperCase());
        if (ix < 0) {
            return FUNCTION_INDEX_EXTERNAL;
        }
        return ix;
    }

    public final boolean isBaseToken() {
        return false;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(64);
        sb.append(getClass().getName()).append(" [");
        sb.append(lookupName(_functionIndex));
        sb.append(" nArgs=").append(_numberOfArgs);
        sb.append("]");
        return sb.toString();
    }

    public final short getFunctionIndex() {
        return _functionIndex;
    }

    public final int getNumberOfOperands() {
        return _numberOfArgs;
    }

    public final String getName() {
        return lookupName(_functionIndex);
    }


    public final boolean isExternalFunction() {
        return _functionIndex == FUNCTION_INDEX_EXTERNAL;
    }

    public final String toFormulaString() {
        return getName();
    }

    public String toFormulaString(String[] operands) {
        StringBuilder buf = new StringBuilder();

        if (isExternalFunction()) {
            buf.append(operands[0]);
            appendArgs(buf, 1, operands);
        } else {
            buf.append(getName());
            appendArgs(buf, 0, operands);
        }
        return buf.toString();
    }

    public abstract int getSize();

    protected final String lookupName(short index) {
        if (index == FunctionMetadataRegistry.FUNCTION_INDEX_EXTERNAL) {
            return "#external#";
        }
        FunctionMetadata fm = FunctionMetadataRegistry.getFunctionByIndex(index);
        if (fm == null) {
            throw new RuntimeException("bad function index (" + index + ")");
        }
        return fm.getName();
    }

    public byte getDefaultOperandClass() {
        return returnClass;
    }

    public final byte getParameterClass(int index) {
        if (index >= paramClass.length) {



            return paramClass[paramClass.length - 1];
        }
        return paramClass[index];
    }
}
