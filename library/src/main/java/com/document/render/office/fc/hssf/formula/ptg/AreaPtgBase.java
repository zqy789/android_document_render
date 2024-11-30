

package com.document.render.office.fc.hssf.formula.ptg;


import com.document.render.office.fc.ss.util.AreaReference;
import com.document.render.office.fc.ss.util.CellReference;
import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;



public abstract class AreaPtgBase extends OperandPtg implements AreaI {
    private final static BitField rowRelative = BitFieldFactory.getInstance(0x8000);
    private final static BitField colRelative = BitFieldFactory.getInstance(0x4000);
    private final static BitField columnMask = BitFieldFactory.getInstance(0x3FFF);

    private int field_1_first_row;

    private int field_2_last_row;

    private int field_3_first_column;

    private int field_4_last_column;
    protected AreaPtgBase() {

    }

    protected AreaPtgBase(AreaReference ar) {
        CellReference firstCell = ar.getFirstCell();
        CellReference lastCell = ar.getLastCell();
        setFirstRow(firstCell.getRow());
        setFirstColumn(firstCell.getCol() == -1 ? 0 : firstCell.getCol());
        setLastRow(lastCell.getRow());
        setLastColumn(lastCell.getCol() == -1 ? 0xFF : lastCell.getCol());
        setFirstColRelative(!firstCell.isColAbsolute());
        setLastColRelative(!lastCell.isColAbsolute());
        setFirstRowRelative(!firstCell.isRowAbsolute());
        setLastRowRelative(!lastCell.isRowAbsolute());
    }

    protected AreaPtgBase(int firstRow, int lastRow, int firstColumn, int lastColumn,
                          boolean firstRowRelative, boolean lastRowRelative, boolean firstColRelative, boolean lastColRelative) {

        if (lastRow > firstRow) {
            setFirstRow(firstRow);
            setLastRow(lastRow);
            setFirstRowRelative(firstRowRelative);
            setLastRowRelative(lastRowRelative);
        } else {
            setFirstRow(lastRow);
            setLastRow(firstRow);
            setFirstRowRelative(lastRowRelative);
            setLastRowRelative(firstRowRelative);
        }

        if (lastColumn > firstColumn) {
            setFirstColumn(firstColumn);
            setLastColumn(lastColumn);
            setFirstColRelative(firstColRelative);
            setLastColRelative(lastColRelative);
        } else {
            setFirstColumn(lastColumn);
            setLastColumn(firstColumn);
            setFirstColRelative(lastColRelative);
            setLastColRelative(firstColRelative);
        }
    }


    protected final RuntimeException notImplemented() {
        return new RuntimeException("Coding Error: This method should never be called. This ptg should be converted");
    }

    protected final void readCoordinates(LittleEndianInput in) {
        field_1_first_row = in.readUShort();
        field_2_last_row = in.readUShort();
        field_3_first_column = in.readUShort();
        field_4_last_column = in.readUShort();
    }

    protected final void writeCoordinates(LittleEndianOutput out) {
        out.writeShort(field_1_first_row);
        out.writeShort(field_2_last_row);
        out.writeShort(field_3_first_column);
        out.writeShort(field_4_last_column);
    }


    public final int getFirstRow() {
        return field_1_first_row;
    }


    public final void setFirstRow(int rowIx) {
        field_1_first_row = rowIx;
    }


    public final int getLastRow() {
        return field_2_last_row;
    }


    public final void setLastRow(int rowIx) {
        field_2_last_row = rowIx;
    }


    public final int getFirstColumn() {
        return columnMask.getValue(field_3_first_column);
    }


    public final void setFirstColumn(int colIx) {
        field_3_first_column = columnMask.setValue(field_3_first_column, colIx);
    }


    public final short getFirstColumnRaw() {
        return (short) field_3_first_column;
    }


    public final void setFirstColumnRaw(int column) {
        field_3_first_column = column;
    }


    public final boolean isFirstRowRelative() {
        return rowRelative.isSet(field_3_first_column);
    }


    public final void setFirstRowRelative(boolean rel) {
        field_3_first_column = rowRelative.setBoolean(field_3_first_column, rel);
    }


    public final boolean isFirstColRelative() {
        return colRelative.isSet(field_3_first_column);
    }


    public final void setFirstColRelative(boolean rel) {
        field_3_first_column = colRelative.setBoolean(field_3_first_column, rel);
    }


    public final int getLastColumn() {
        return columnMask.getValue(field_4_last_column);
    }


    public final void setLastColumn(int colIx) {
        field_4_last_column = columnMask.setValue(field_4_last_column, colIx);
    }


    public final short getLastColumnRaw() {
        return (short) field_4_last_column;
    }


    public final void setLastColumnRaw(short column) {
        field_4_last_column = column;
    }


    public final boolean isLastRowRelative() {
        return rowRelative.isSet(field_4_last_column);
    }


    public final void setLastRowRelative(boolean rel) {
        field_4_last_column = rowRelative.setBoolean(field_4_last_column, rel);
    }


    public final boolean isLastColRelative() {
        return colRelative.isSet(field_4_last_column);
    }


    public final void setLastColRelative(boolean rel) {
        field_4_last_column = colRelative.setBoolean(field_4_last_column, rel);
    }

    protected final String formatReferenceAsString() {
        CellReference topLeft = new CellReference(getFirstRow(), getFirstColumn(), !isFirstRowRelative(), !isFirstColRelative());
        CellReference botRight = new CellReference(getLastRow(), getLastColumn(), !isLastRowRelative(), !isLastColRelative());

        if (AreaReference.isWholeColumnReference(topLeft, botRight)) {
            return (new AreaReference(topLeft, botRight)).formatAsString();
        }
        return topLeft.formatAsString() + ":" + botRight.formatAsString();
    }

    public String toFormulaString() {
        return formatReferenceAsString();
    }

    public byte getDefaultOperandClass() {
        return Ptg.CLASS_REF;
    }
}
