

package com.document.render.office.ss.model.baseModel;

import com.document.render.office.common.bg.BackgroundAndFill;
import com.document.render.office.constant.SSConstant;
import com.document.render.office.ss.model.style.CellStyle;
import com.document.render.office.ss.other.ExpandedCellRangeAddress;

import java.util.Collection;
import java.util.Hashtable;



public class Row {

    protected Sheet sheet;

    protected int firstCol;

    protected int lastCol;

    protected int rowNumber;

    protected int styleIndex;

    protected Hashtable<Integer, Cell> cells;

    private float rowPixelHeight = SSConstant.DEFAULT_ROW_HEIGHT;
    private RowProperty rowProp;


    public Row(int capacity) {
        this.lastCol = capacity;


        cells = new Hashtable<Integer, Cell>(capacity);


        rowProp = new RowProperty();
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }


    private Cell retrieveCell(int cellIndex, boolean style) {
        try {
            if (cellIndex < 0) {
                return null;
            }

            Cell cell = cells.get(cellIndex);
            if (cell == null && style) {

                cell = createCellByStyle(styleIndex, cellIndex);
                if (cell == null) {

                    cell = createCellByStyle(sheet.getColumnStyle(cellIndex), cellIndex);
                }
            }

            return cell;
        } catch (Exception e) {
            return null;
        }
    }

    private Cell createCellByStyle(int styleIndex, int column) {
        Cell cell = null;
        CellStyle cellStyle = sheet.getWorkbook().getCellStyle(styleIndex);
        if (cellStyle != null
                && ((cellStyle.getFillPatternType() == BackgroundAndFill.FILL_SOLID && (cellStyle.getFgColor() & 0xFFFFFF) != 0xFFFFFF)
                || cellStyle.getBorderLeft() > 0
                || cellStyle.getBorderTop() > 0
                || cellStyle.getBorderRight() > 0
                || cellStyle.getBorderBottom() > 0)) {
            cell = new Cell(Cell.CELL_TYPE_NUMERIC);
            cell.setColNumber(column);
            cell.setRowNumber(rowNumber);
            cell.setCellStyle(styleIndex);
            cell.setSheet(sheet);

            cells.put(column, cell);
        }

        return cell;
    }


    public Cell getCell(int cellnum) {
        return retrieveCell(cellnum, true);
    }


    public Cell getCell(int cellnum, boolean style) {
        return retrieveCell(cellnum, style);
    }


    public Collection<Cell> cellCollection() {
        return cells.values();
    }


    public void addCell(Cell cell) {
        int column = cell.getColNumber();
        cells.put(column, cell);


        firstCol = Math.min(firstCol, column);
        lastCol = Math.max(lastCol, column + 1);
    }


    public int getFirstCol() {
        return firstCol;
    }


    public void setFirstCol(int firstCol) {
        this.firstCol = firstCol;
    }


    public int getLastCol() {
        return lastCol;
    }


    public void setLastCol(int lastCol) {
        this.lastCol = lastCol;
    }


    public int getRowStyle() {
        return styleIndex;
    }


    public void setRowStyle(int styleIndex) {
        this.styleIndex = styleIndex;
    }


    public float getRowPixelHeight() {
        return rowPixelHeight;
    }


    public void setRowPixelHeight(float rowPixelHeight) {
        this.rowPixelHeight = rowPixelHeight;
    }


    public int getRowNumber() {
        return rowNumber;
    }


    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }


    public boolean isEmpty() {
        return cells.size() == 0;
    }


    public boolean isZeroHeight() {
        return rowProp.isZeroHeight();
    }


    public void setZeroHeight(boolean zeroHeight) {
        rowProp.setRowProperty(RowProperty.ROWPROPID_ZEROHEIGHT, zeroHeight);
    }


    public int getPhysicalNumberOfCells() {
        return cells.size();
    }


    public void removeAllCells() {
        Collection<Cell> cellCollection = cells.values();
        for (Cell cell : cellCollection) {
            cell.dispose();
        }
        cells.clear();
    }


    public void removeCellsForHiddenRow() {
        if (!rowProp.isZeroHeight()) {
            return;
        }

        Collection<Cell> cellCollection = cells.values();
        for (Cell cell : cellCollection) {
            if (cell.getRangeAddressIndex() >= 0) {
                continue;
            }
            cell.dispose();
        }
    }


    public void completed() {
        rowProp.setRowProperty(RowProperty.ROWPROPID_COMPLETED, true);
    }


    public boolean isCompleted() {
        return rowProp.isCompleted();
    }


    public boolean isInitExpandedRangeAddress() {
        return rowProp.isInitExpandedRangeAddr();
    }


    public void setInitExpandedRangeAddress(boolean init) {
        rowProp.setRowProperty(RowProperty.ROWPROPID_INITEXPANDEDRANGEADDR, init);
    }


    public void addExpandedRangeAddress(int index, ExpandedCellRangeAddress addr) {
        rowProp.setRowProperty(RowProperty.ROWPROPID_EXPANDEDRANGEADDRLIST, addr);
    }


    public int getExpandedCellCount() {
        return rowProp.getExpandedCellCount();
    }


    public ExpandedCellRangeAddress getExpandedRangeAddress(int index) {
        return rowProp.getExpandedCellRangeAddr(index);
    }


    public void dispose() {
        removeAllCells();

        if (rowProp != null) {
            rowProp.dispose();
            rowProp = null;
        }

        sheet = null;
        cells = null;
    }
}
