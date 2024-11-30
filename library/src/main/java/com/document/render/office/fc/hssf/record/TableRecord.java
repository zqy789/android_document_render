

package com.document.render.office.fc.hssf.record;


import androidx.annotation.Keep;

import com.document.render.office.fc.hssf.formula.ptg.TblPtg;
import com.document.render.office.fc.hssf.util.CellRangeAddress8Bit;
import com.document.render.office.fc.hssf.util.CellReference;
import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class TableRecord extends SharedValueRecordBase {
    @Keep
    public static final short sid = 0x0236;

    private static final BitField alwaysCalc = BitFieldFactory.getInstance(0x0001);
    private static final BitField calcOnOpen = BitFieldFactory.getInstance(0x0002);
    private static final BitField rowOrColInpCell = BitFieldFactory.getInstance(0x0004);
    private static final BitField oneOrTwoVar = BitFieldFactory.getInstance(0x0008);
    private static final BitField rowDeleted = BitFieldFactory.getInstance(0x0010);
    private static final BitField colDeleted = BitFieldFactory.getInstance(0x0020);

    private int field_5_flags;
    private int field_6_res;
    private int field_7_rowInputRow;
    private int field_8_colInputRow;
    private int field_9_rowInputCol;
    private int field_10_colInputCol;

    public TableRecord(RecordInputStream in) {
        super(in);
        field_5_flags = in.readByte();
        field_6_res = in.readByte();
        field_7_rowInputRow = in.readShort();
        field_8_colInputRow = in.readShort();
        field_9_rowInputCol = in.readShort();
        field_10_colInputCol = in.readShort();
    }

    public TableRecord(CellRangeAddress8Bit range) {
        super(range);
        field_6_res = 0;
    }

    private static CellReference cr(int rowIx, int colIxAndFlags) {
        int colIx = colIxAndFlags & 0x00FF;
        boolean isRowAbs = (colIxAndFlags & 0x8000) == 0;
        boolean isColAbs = (colIxAndFlags & 0x4000) == 0;
        return new CellReference(rowIx, colIx, isRowAbs, isColAbs);
    }

    public int getFlags() {
        return field_5_flags;
    }

    public void setFlags(int flags) {
        field_5_flags = flags;
    }

    public int getRowInputRow() {
        return field_7_rowInputRow;
    }

    public void setRowInputRow(int rowInputRow) {
        field_7_rowInputRow = rowInputRow;
    }

    public int getColInputRow() {
        return field_8_colInputRow;
    }

    public void setColInputRow(int colInputRow) {
        field_8_colInputRow = colInputRow;
    }

    public int getRowInputCol() {
        return field_9_rowInputCol;
    }

    public void setRowInputCol(int rowInputCol) {
        field_9_rowInputCol = rowInputCol;
    }

    public int getColInputCol() {
        return field_10_colInputCol;
    }

    public void setColInputCol(int colInputCol) {
        field_10_colInputCol = colInputCol;
    }

    public boolean isAlwaysCalc() {
        return alwaysCalc.isSet(field_5_flags);
    }

    public void setAlwaysCalc(boolean flag) {
        field_5_flags = alwaysCalc.setBoolean(field_5_flags, flag);
    }

    public boolean isRowOrColInpCell() {
        return rowOrColInpCell.isSet(field_5_flags);
    }

    public void setRowOrColInpCell(boolean flag) {
        field_5_flags = rowOrColInpCell.setBoolean(field_5_flags, flag);
    }

    public boolean isOneNotTwoVar() {
        return oneOrTwoVar.isSet(field_5_flags);
    }

    public void setOneNotTwoVar(boolean flag) {
        field_5_flags = oneOrTwoVar.setBoolean(field_5_flags, flag);
    }

    public boolean isColDeleted() {
        return colDeleted.isSet(field_5_flags);
    }

    public void setColDeleted(boolean flag) {
        field_5_flags = colDeleted.setBoolean(field_5_flags, flag);
    }

    public boolean isRowDeleted() {
        return rowDeleted.isSet(field_5_flags);
    }

    public void setRowDeleted(boolean flag) {
        field_5_flags = rowDeleted.setBoolean(field_5_flags, flag);
    }

    public short getSid() {
        return sid;
    }

    protected int getExtraDataSize() {
        return
                2
                        + 8;
    }

    protected void serializeExtraData(LittleEndianOutput out) {
        out.writeByte(field_5_flags);
        out.writeByte(field_6_res);
        out.writeShort(field_7_rowInputRow);
        out.writeShort(field_8_colInputRow);
        out.writeShort(field_9_rowInputCol);
        out.writeShort(field_10_colInputCol);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[TABLE]\n");
        buffer.append("    .range    = ").append(getRange().toString()).append("\n");
        buffer.append("    .flags    = ").append(HexDump.byteToHex(field_5_flags)).append("\n");
        buffer.append("    .alwaysClc= ").append(isAlwaysCalc()).append("\n");
        buffer.append("    .reserved = ").append(HexDump.intToHex(field_6_res)).append("\n");
        CellReference crRowInput = cr(field_7_rowInputRow, field_8_colInputRow);
        CellReference crColInput = cr(field_9_rowInputCol, field_10_colInputCol);
        buffer.append("    .rowInput = ").append(crRowInput.formatAsString()).append("\n");
        buffer.append("    .colInput = ").append(crColInput.formatAsString()).append("\n");
        buffer.append("[/TABLE]\n");
        return buffer.toString();
    }
}
