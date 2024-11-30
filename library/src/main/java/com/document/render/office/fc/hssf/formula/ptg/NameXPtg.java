

package com.document.render.office.fc.hssf.formula.ptg;


import com.document.render.office.fc.hssf.formula.FormulaRenderingWorkbook;
import com.document.render.office.fc.hssf.formula.WorkbookDependentFormula;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class NameXPtg extends OperandPtg implements WorkbookDependentFormula {
    public final static short sid = 0x39;
    private final static int SIZE = 7;

    
    private final int _sheetRefIndex;
    
    private final int _nameNumber;
    
    private final int _reserved;

    private NameXPtg(int sheetRefIndex, int nameNumber, int reserved) {
        _sheetRefIndex = sheetRefIndex;
        _nameNumber = nameNumber;
        _reserved = reserved;
    }

    
    public NameXPtg(int sheetRefIndex, int nameIndex) {
        this(sheetRefIndex, nameIndex + 1, 0);
    }

    public NameXPtg(LittleEndianInput in) {
        this(in.readUShort(), in.readUShort(), in.readUShort());
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(sid + getPtgClass());
        out.writeShort(_sheetRefIndex);
        out.writeShort(_nameNumber);
        out.writeShort(_reserved);
    }

    public int getSize() {
        return SIZE;
    }

    public String toFormulaString(FormulaRenderingWorkbook book) {
        
        return book.resolveNameXText(this);
    }

    public String toFormulaString() {
        throw new RuntimeException("3D references need a workbook to determine formula text");
    }

    public String toString() {
        String retValue = "NameXPtg:[sheetRefIndex:" + _sheetRefIndex +
                " , nameNumber:" + _nameNumber + "]";
        return retValue;
    }

    public byte getDefaultOperandClass() {
        return Ptg.CLASS_VALUE;
    }

    public int getSheetRefIndex() {
        return _sheetRefIndex;
    }

    public int getNameIndex() {
        return _nameNumber - 1;
    }
}
