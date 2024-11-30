

package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.fc.hssf.record.CellValueRecordInterface;
import com.document.render.office.fc.hssf.record.ExtendedFormatRecord;
import com.document.render.office.fc.hssf.record.RowRecord;
import com.document.render.office.fc.ss.SpreadsheetVersion;
import com.document.render.office.fc.ss.usermodel.ICell;
import com.document.render.office.fc.ss.usermodel.ICellStyle;
import com.document.render.office.fc.ss.usermodel.IRow;

import java.util.Iterator;
import java.util.NoSuchElementException;



public final class HSSFRow implements IRow {


    public final static int INITIAL_CAPACITY = 5;

    private int rowNum;
    private HSSFCell[] cells;


    private RowRecord row;


    private HSSFWorkbook book;


    private HSSFSheet sheet;


    private int rowPixelHeight = 18;


    HSSFRow(HSSFWorkbook book, HSSFSheet sheet, int rowNum) {
        this(book, sheet, new RowRecord(rowNum));
    }


    HSSFRow(HSSFWorkbook book, HSSFSheet sheet, RowRecord record) {
        this.book = book;
        this.sheet = sheet;
        row = record;
        setRowNum(record.getRowNumber());




        cells = new HSSFCell[record.getLastCol() + INITIAL_CAPACITY];



        record.setEmpty();

    }


    public HSSFCell createCell(short columnIndex) {
        return createCell((int) columnIndex);
    }


    public HSSFCell createCell(short columnIndex, int type) {
        return createCell((int) columnIndex, type);
    }


    public HSSFCell createCell(int column) {
        return this.createCell(column, HSSFCell.CELL_TYPE_BLANK);
    }


    public HSSFCell createCell(int columnIndex, int type) {
        short shortCellNum = (short) columnIndex;
        if (columnIndex > 0x7FFF) {
            shortCellNum = (short) (0xffff - columnIndex);
        }

        HSSFCell cell = new HSSFCell(book, sheet, getRowNum(), shortCellNum, type);
        addCell(cell);
        sheet.getSheet().addValueRecord(getRowNum(), cell.getCellValueRecord());
        return cell;
    }


    public void removeCell(ICell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("cell must not be null");
        }
        removeCell((HSSFCell) cell, true);
    }

    private void removeCell(HSSFCell cell, boolean alsoRemoveRecords) {

        int column = cell.getColumnIndex();
        if (column < 0) {
            throw new RuntimeException("Negative cell indexes not allowed");
        }
        if (column >= cells.length || cell != cells[column]) {
            throw new RuntimeException("Specified cell is not from this row");
        }
        if (cell.isPartOfArrayFormulaGroup()) {
            cell.notifyArrayFormulaChanging();
        }

        cells[column] = null;

        if (alsoRemoveRecords) {
            CellValueRecordInterface cval = cell.getCellValueRecord();
            sheet.getSheet().removeValueRecord(getRowNum(), cval);
        }
        if (cell.getColumnIndex() + 1 == row.getLastCol()) {
            row.setLastCol(calculateNewLastCellPlusOne(row.getLastCol()));
        }
        if (cell.getColumnIndex() == row.getFirstCol()) {
            row.setFirstCol(calculateNewFirstCell(row.getFirstCol()));
        }
    }


    protected void removeAllCells() {
        for (int i = 0; i < cells.length; i++) {
            if (cells[i] != null) {
                removeCell(cells[i], true);
            }
        }
        cells = new HSSFCell[INITIAL_CAPACITY];
    }


    HSSFCell createCellFromRecord(CellValueRecordInterface cell) {
        HSSFCell hcell = new HSSFCell(book, sheet, cell);

        addCell(hcell);
        int colIx = cell.getColumn();
        if (row.isEmpty()) {
            row.setFirstCol(colIx);
            row.setLastCol(colIx + 1);
        } else {
            if (colIx < row.getFirstCol()) {
                row.setFirstCol(colIx);
            } else if (colIx > row.getLastCol()) {
                row.setLastCol(colIx + 1);
            } else {

            }
        }

        return hcell;
    }


    public int getRowNum() {
        return rowNum;
    }


    public void setRowNum(int rowIndex) {
        int maxrow = SpreadsheetVersion.EXCEL97.getLastRowIndex();
        if ((rowIndex < 0) || (rowIndex > maxrow)) {
            throw new IllegalArgumentException("Invalid row number (" + rowIndex
                    + ") outside allowable range (0.." + maxrow + ")");
        }
        rowNum = rowIndex;
        if (row != null) {
            row.setRowNumber(rowIndex);
        }
    }


    public HSSFSheet getSheet() {
        return sheet;
    }


    protected int getOutlineLevel() {
        return row.getOutlineLevel();
    }


    public void moveCell(HSSFCell cell, short newColumn) {

        if (cells.length > newColumn && cells[newColumn] != null) {
            throw new IllegalArgumentException("Asked to move cell to column " + newColumn + " but there's already a cell there");
        }


        if (!cells[cell.getColumnIndex()].equals(cell)) {
            throw new IllegalArgumentException("Asked to move a cell, but it didn't belong to our row");
        }



        removeCell(cell, false);
        cell.updateCellNum(newColumn);
        addCell(cell);
    }


    private void addCell(HSSFCell cell) {

        int column = cell.getColumnIndex();

        if (column >= cells.length) {
            HSSFCell[] oldCells = cells;

            int newSize = oldCells.length * 3 / 2 + 1;
            if (newSize < column + 1) {
                newSize = column + INITIAL_CAPACITY;
            }
            cells = new HSSFCell[newSize];
            System.arraycopy(oldCells, 0, cells, 0, oldCells.length);
        }
        cells[column] = cell;


        if (row.isEmpty() || column < row.getFirstCol()) {
            row.setFirstCol((short) column);
        }

        if (row.isEmpty() || column >= row.getLastCol()) {
            row.setLastCol((short) (column + 1));
        }
    }


    private HSSFCell retrieveCell(int cellIndex) {
        if (cellIndex < 0 || cellIndex >= cells.length) {
            return null;
        }
        return cells[cellIndex];
    }


    public HSSFCell getCell(short cellnum) {
        int ushortCellNum = cellnum & 0x0000FFFF;
        return getCell(ushortCellNum);
    }


    public HSSFCell getCell(int cellnum) {
        return getCell(cellnum, book.getMissingCellPolicy());
    }


    public HSSFCell getCell(int cellnum, MissingCellPolicy policy) {
        HSSFCell cell = retrieveCell(cellnum);
        if (policy == RETURN_NULL_AND_BLANK) {
            return cell;
        }
        if (policy == RETURN_BLANK_AS_NULL) {
            if (cell == null) return cell;
            if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                return null;
            }
            return cell;
        }
        if (policy == CREATE_NULL_AS_BLANK) {
            if (cell == null) {
                return createCell(cellnum, HSSFCell.CELL_TYPE_BLANK);
            }
            return cell;
        }
        throw new IllegalArgumentException("Illegal policy " + policy + " (" + policy.id + ")");
    }


    public short getFirstCellNum() {
        if (row.isEmpty()) {
            return -1;
        }
        return (short) row.getFirstCol();
    }


    public short getLastCellNum() {
        if (row.isEmpty()) {
            return -1;
        }
        return (short) row.getLastCol();
    }




    public int getPhysicalNumberOfCells() {
        int count = 0;
        for (int i = 0; i < cells.length; i++) {
            if (cells[i] != null) count++;
        }
        return count;
    }


    public boolean getZeroHeight() {
        return row.getZeroHeight();
    }


    public void setZeroHeight(boolean zHeight) {
        row.setZeroHeight(zHeight);
    }



    public short getHeight() {
        short height = row.getHeight();



        if ((height & 0x8000) != 0) height = sheet.getSheet().getDefaultRowHeight();
        else height &= 0x7FFF;

        return height;
    }



    public void setHeight(short height) {
        if (height == -1) {
            row.setHeight((short) (0xFF | 0x8000));
        } else {
            row.setBadFontHeight(true);
            row.setHeight(height);
        }
    }



    public float getHeightInPoints() {
        return ((float) getHeight() / 20);
    }



    public void setHeightInPoints(float height) {
        if (height == -1) {
            row.setHeight((short) (0xFF | 0x8000));
        } else {
            row.setBadFontHeight(true);
            row.setHeight((short) (height * 20));
        }
    }



    protected RowRecord getRowRecord() {
        return row;
    }


    private int calculateNewLastCellPlusOne(int lastcell) {
        int cellIx = lastcell - 1;
        HSSFCell r = retrieveCell(cellIx);

        while (r == null) {
            if (cellIx < 0) {
                return 0;
            }
            r = retrieveCell(--cellIx);
        }
        return cellIx + 1;
    }


    private int calculateNewFirstCell(int firstcell) {
        int cellIx = firstcell + 1;
        HSSFCell r = retrieveCell(cellIx);

        while (r == null) {
            if (cellIx <= cells.length) {
                return 0;
            }
            r = retrieveCell(++cellIx);
        }
        return cellIx;
    }


    public boolean isFormatted() {
        return row.getFormatted();
    }


    public HSSFCellStyle getRowStyle() {
        if (!isFormatted()) {
            return null;
        }
        short styleIndex = row.getXFIndex();
        ExtendedFormatRecord xf = book.getWorkbook().getExFormatAt(styleIndex);
        return new HSSFCellStyle(styleIndex, xf, book);
    }


    public void setRowStyle(HSSFCellStyle style) {
        row.setFormatted(true);
        row.setXFIndex(style.getIndex());
    }


    public void setRowStyle(ICellStyle style) {
        setRowStyle((HSSFCellStyle) style);
    }

    public int getRowStyleIndex() {
        if (!isFormatted()) {
            return 0;
        }

        return row.getXFIndex();
    }


    public Iterator<ICell> cellIterator() {
        return new CellIterator();
    }


    public Iterator iterator() {
        return cellIterator();
    }

    public int compareTo(Object obj) {
        HSSFRow loc = (HSSFRow) obj;

        if (this.getRowNum() == loc.getRowNum()) {
            return 0;
        }
        if (this.getRowNum() < loc.getRowNum()) {
            return -1;
        }
        if (this.getRowNum() > loc.getRowNum()) {
            return 1;
        }
        return -1;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof HSSFRow)) {
            return false;
        }
        HSSFRow loc = (HSSFRow) obj;

        if (this.getRowNum() == loc.getRowNum()) {
            return true;
        }
        return false;
    }


    public int getRowPixelHeight() {
        return rowPixelHeight;
    }


    public void setRowPixelHeight(int rowPixelHeight) {
        this.rowPixelHeight = rowPixelHeight;
    }


    public boolean isEmpty() {
        return row.isEmpty();
    }


    private class CellIterator implements Iterator<ICell> {
        int thisId = -1;
        int nextId = -1;

        public CellIterator() {
            findNext();
        }

        public boolean hasNext() {
            return nextId < cells.length;
        }

        public ICell next() {
            if (!hasNext())
                throw new NoSuchElementException("At last element");
            HSSFCell cell = cells[nextId];
            thisId = nextId;
            findNext();
            return cell;
        }

        public void remove() {
            if (thisId == -1)
                throw new IllegalStateException("remove() called before next()");
            cells[thisId] = null;
        }

        private void findNext() {
            int i = nextId + 1;
            for (; i < cells.length; i++) {
                if (cells[i] != null) break;
            }
            nextId = i;
        }

    }
}
