

package com.document.render.office.fc.hssf.formula.ptg;


import com.document.render.office.fc.hssf.formula.ExternSheetReferenceToken;
import com.document.render.office.fc.hssf.formula.FormulaRenderingWorkbook;
import com.document.render.office.fc.hssf.formula.WorkbookDependentFormula;
import com.document.render.office.fc.ss.util.CellReference;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class Ref3DPtg extends RefPtgBase implements WorkbookDependentFormula, ExternSheetReferenceToken {
    public final static byte sid = 0x3a;

    private final static int SIZE = 7;
    private int field_1_index_extern_sheet;


    public Ref3DPtg(LittleEndianInput in) {
        field_1_index_extern_sheet = in.readShort();
        readCoordinates(in);
    }

    public Ref3DPtg(String cellref, int externIdx) {
        this(new CellReference(cellref), externIdx);
    }

    public Ref3DPtg(CellReference c, int externIdx) {
        super(c);
        setExternSheetIndex(externIdx);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName());
        sb.append(" [");
        sb.append("sheetIx=").append(getExternSheetIndex());
        sb.append(" ! ");
        sb.append(formatReferenceAsString());
        sb.append("]");
        return sb.toString();
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(sid + getPtgClass());
        out.writeShort(getExternSheetIndex());
        writeCoordinates(out);
    }

    public int getSize() {
        return SIZE;
    }

    public int getExternSheetIndex() {
        return field_1_index_extern_sheet;
    }

    public void setExternSheetIndex(int index) {
        field_1_index_extern_sheet = index;
    }

    public String format2DRefAsString() {
        return formatReferenceAsString();
    }


    public String toFormulaString(FormulaRenderingWorkbook book) {
        return ExternSheetNameResolver.prependSheetName(book, field_1_index_extern_sheet, formatReferenceAsString());
    }

    public String toFormulaString() {
        throw new RuntimeException("3D references need a workbook to determine formula text");
    }
}
