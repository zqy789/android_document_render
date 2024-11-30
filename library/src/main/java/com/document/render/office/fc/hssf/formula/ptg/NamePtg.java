

package com.document.render.office.fc.hssf.formula.ptg;


import com.document.render.office.fc.hssf.formula.FormulaRenderingWorkbook;
import com.document.render.office.fc.hssf.formula.WorkbookDependentFormula;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class NamePtg extends OperandPtg implements WorkbookDependentFormula {
    public final static short sid = 0x23;
    private final static int SIZE = 5;

    private int field_1_label_index;
    private short field_2_zero;


    public NamePtg(int nameIndex) {
        field_1_label_index = 1 + nameIndex;
    }



    public NamePtg(LittleEndianInput in) {
        field_1_label_index = in.readShort();
        field_2_zero = in.readShort();
    }


    public int getIndex() {
        return field_1_label_index - 1;
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(sid + getPtgClass());
        out.writeShort(field_1_label_index);
        out.writeShort(field_2_zero);
    }

    public int getSize() {
        return SIZE;
    }

    public String toFormulaString(FormulaRenderingWorkbook book) {
        return book.getNameText(this);
    }

    public String toFormulaString() {
        throw new RuntimeException("3D references need a workbook to determine formula text");
    }

    public byte getDefaultOperandClass() {
        return Ptg.CLASS_REF;
    }
}
