

package com.document.render.office.fc.hssf.formula.ptg;

import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class MemErrPtg extends OperandPtg {
    public final static short sid = 0x27;
    private final static int SIZE = 7;
    private int field_1_reserved;
    private short field_2_subex_len;

    public MemErrPtg(LittleEndianInput in) {
        field_1_reserved = in.readInt();
        field_2_subex_len = in.readShort();
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(sid + getPtgClass());
        out.writeInt(field_1_reserved);
        out.writeShort(field_2_subex_len);
    }

    public int getSize() {
        return SIZE;
    }

    public String toFormulaString() {
        return "ERR#";
    }

    public byte getDefaultOperandClass() {
        return Ptg.CLASS_VALUE;
    }
}
