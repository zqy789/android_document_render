

package com.document.render.office.fc.hssf.formula.ptg;


import androidx.annotation.Keep;

import com.document.render.office.fc.hssf.formula.function.FunctionMetadata;
import com.document.render.office.fc.hssf.formula.function.FunctionMetadataRegistry;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class FuncPtg extends AbstractFunctionPtg {

    public final static byte sid = 0x21;
    public final static int SIZE = 3;

    private FuncPtg(int funcIndex, FunctionMetadata fm) {
        super(funcIndex, fm.getReturnClassCode(), fm.getParameterClassCodes(), fm.getMinParams());
    }

    @Keep
    public static FuncPtg create(LittleEndianInput in) {
        return create(in.readUShort());
    }

    @Keep
    public static FuncPtg create(int functionIndex) {
        FunctionMetadata fm = FunctionMetadataRegistry.getFunctionByIndex(functionIndex);
        if (fm == null) {
            throw new RuntimeException("Invalid built-in function index (" + functionIndex + ")");
        }
        return new FuncPtg(functionIndex, fm);
    }


    public void write(LittleEndianOutput out) {
        out.writeByte(sid + getPtgClass());
        out.writeShort(getFunctionIndex());
    }

    public int getSize() {
        return SIZE;
    }
}
