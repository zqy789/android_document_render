
package com.document.render.office.common.shape;


public class TableShape extends AbstractShape {

    private TableCell[] cells;
    private int rowCnt;
    private int colCnt;

    private boolean shape07 = true;

    private boolean firstRow = false;
    private boolean lastRow = false;
    private boolean firstCol = false;
    private boolean lastCol = false;
    private boolean bandRow = false;
    private boolean bandCol = false;


    public TableShape(int numrows, int numcols) {
        if (numrows < 1)
            throw new IllegalArgumentException("The number of rows must be greater than 1");
        if (numcols < 1)
            throw new IllegalArgumentException("The number of columns must be greater than 1");

        this.rowCnt = numrows;
        this.colCnt = numcols;

        cells = new TableCell[numrows * numcols];
    }


    public short getType() {
        return SHAPE_TABLE;
    }


    public TableCell getCell(int index) {
        if (index >= cells.length) {
            return null;
        }
        return cells[index];
    }


    public void addCell(int index, TableCell tableCell) {
        cells[index] = tableCell;
    }


    public int getCellCount() {
        return cells.length;
    }


    public boolean isTable07() {
        return shape07;
    }


    public void setTable07(boolean shape07) {
        this.shape07 = shape07;
    }

    public boolean isFirstRow() {
        return firstRow;
    }

    public void setFirstRow(boolean firstRow) {
        this.firstRow = firstRow;
    }

    public boolean isLastRow() {
        return lastRow;
    }

    public void setLastRow(boolean lastRow) {
        this.lastRow = lastRow;
    }

    public boolean isFirstCol() {
        return firstCol;
    }

    public void setFirstCol(boolean firstCol) {
        this.firstCol = firstCol;
    }

    public boolean isLastCol() {
        return lastCol;
    }

    public void setLastCol(boolean lastCol) {
        this.lastCol = lastCol;
    }

    public boolean isBandRow() {
        return bandRow;
    }

    public void setBandRow(boolean bandRow) {
        this.bandRow = bandRow;
    }

    public boolean isBandCol() {
        return bandCol;
    }

    public void setBandCol(boolean bandCol) {
        this.bandCol = bandCol;
    }

    public int getRowCount() {
        return rowCnt;
    }

    public void setRowCount(int rowCnt) {
        this.rowCnt = rowCnt;
    }

    public int getColumnCount() {
        return colCnt;
    }

    public void setColumnCount(int colCnt) {
        this.colCnt = colCnt;
    }


    public void dispose() {
        if (cells != null) {
            for (int i = 0; i < cells.length; i++) {
                TableCell cell = cells[i];
                if (cell != null) {
                    cell.dispose();
                }
            }
            cells = null;
        }
    }
}
