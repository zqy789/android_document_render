

package com.document.render.office.fc.hssf.formula.ptg;


import com.document.render.office.fc.hssf.formula.ExternSheetReferenceToken;
import com.document.render.office.fc.hssf.formula.FormulaRenderingWorkbook;
import com.document.render.office.fc.hssf.formula.WorkbookDependentFormula;
import com.document.render.office.fc.ss.util.AreaReference;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class Area3DPtg extends AreaPtgBase implements WorkbookDependentFormula, ExternSheetReferenceToken {
    public final static byte sid = 0x3b;
    private final static int SIZE = 11;

    private int field_1_index_extern_sheet;


    public Area3DPtg(String arearef, int externIdx) {
        super(new AreaReference(arearef));
        setExternSheetIndex(externIdx);
    }

    public Area3DPtg(LittleEndianInput in) {
        field_1_index_extern_sheet = in.readShort();
        readCoordinates(in);
    }

    public Area3DPtg(int firstRow, int lastRow, int firstColumn, int lastColumn,
                     boolean firstRowRelative, boolean lastRowRelative, boolean firstColRelative, boolean lastColRelative,
                     int externalSheetIndex) {
        super(firstRow, lastRow, firstColumn, lastColumn, firstRowRelative, lastRowRelative, firstColRelative, lastColRelative);
        setExternSheetIndex(externalSheetIndex);
    }

    public Area3DPtg(AreaReference arearef, int externIdx) {
        super(arearef);
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
        out.writeShort(field_1_index_extern_sheet);
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
