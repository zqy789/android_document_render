

package com.document.render.office.fc.hssf.formula.ptg;

import androidx.annotation.Keep;

import com.document.render.office.fc.hssf.formula.function.FunctionMetadata;
import com.document.render.office.fc.hssf.formula.function.FunctionMetadataRegistry;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class FuncVarPtg extends AbstractFunctionPtg {

    public final static byte sid = 0x22;

    public static final OperationPtg SUM = FuncVarPtg.create("SUM", 1);
    private final static int SIZE = 4;

    private FuncVarPtg(int functionIndex, int returnClass, byte[] paramClasses, int numArgs) {
        super(functionIndex, returnClass, paramClasses, numArgs);
    }


    @Keep
    public static FuncVarPtg create(LittleEndianInput in) {
        return create(in.readByte(), in.readShort());
    }


    @Keep
    public static FuncVarPtg create(String pName, int numArgs) {
        return create(numArgs, lookupIndex(pName));
    }

    @Keep
    private static FuncVarPtg create(int numArgs, int functionIndex) {
        FunctionMetadata fm = FunctionMetadataRegistry.getFunctionByIndex(functionIndex);
        if (fm == null) {

            return new FuncVarPtg(functionIndex, Ptg.CLASS_VALUE, new byte[]{Ptg.CLASS_VALUE}, numArgs);
        }
        return new FuncVarPtg(functionIndex, fm.getReturnClassCode(), fm.getParameterClassCodes(), numArgs);
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(sid + getPtgClass());
        out.writeByte(getNumberOfOperands());
        out.writeShort(getFunctionIndex());
    }

    public int getSize() {
        return SIZE;
    }
}
