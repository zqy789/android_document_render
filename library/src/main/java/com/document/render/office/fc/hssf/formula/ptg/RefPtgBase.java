

package com.document.render.office.fc.hssf.formula.ptg;


import com.document.render.office.fc.ss.util.CellReference;
import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;



public abstract class RefPtgBase extends OperandPtg {

    private static final BitField rowRelative = BitFieldFactory.getInstance(0x8000);
    private static final BitField colRelative = BitFieldFactory.getInstance(0x4000);

    private static final BitField column = BitFieldFactory.getInstance(0x3FFF);

    private int field_1_row;

    private int field_2_col;

    protected RefPtgBase() {

    }

    protected RefPtgBase(CellReference c) {
        setRow(c.getRow());
        setColumn(c.getCol());
        setColRelative(!c.isColAbsolute());
        setRowRelative(!c.isRowAbsolute());
    }

    protected final void readCoordinates(LittleEndianInput in) {
        field_1_row = in.readUShort();
        field_2_col = in.readUShort();
    }

    protected final void writeCoordinates(LittleEndianOutput out) {
        out.writeShort(field_1_row);
        out.writeShort(field_2_col);
    }


    public final int getRow() {
        return field_1_row;
    }

    public final void setRow(int rowIndex) {
        field_1_row = rowIndex;
    }

    public final boolean isRowRelative() {
        return rowRelative.isSet(field_2_col);
    }

    public final void setRowRelative(boolean rel) {
        field_2_col = rowRelative.setBoolean(field_2_col, rel);
    }

    public final boolean isColRelative() {
        return colRelative.isSet(field_2_col);
    }

    public final void setColRelative(boolean rel) {
        field_2_col = colRelative.setBoolean(field_2_col, rel);
    }

    public final int getColumn() {
        return column.getValue(field_2_col);
    }

    public final void setColumn(int col) {
        field_2_col = column.setValue(field_2_col, col);
    }

    protected final String formatReferenceAsString() {

        CellReference cr = new CellReference(getRow(), getColumn(), !isRowRelative(), !isColRelative());
        return cr.formatAsString();
    }

    public final byte getDefaultOperandClass() {
        return Ptg.CLASS_REF;
    }
}
