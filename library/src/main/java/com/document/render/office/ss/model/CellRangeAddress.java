
package com.document.render.office.ss.model;


public class CellRangeAddress {
    private int firstRow;
    private int firstCol;
    private int lastRow;
    private int lastCol;


    public CellRangeAddress(int firstRow, int firstCol, int lastRow, int lastCol) {
        this.firstRow = firstRow;
        this.firstCol = firstCol;
        this.lastRow = lastRow;
        this.lastCol = lastCol;
    }


    public int getFirstRow() {
        return firstRow;
    }


    public void setFirstRow(int firstRow) {
        this.firstRow = firstRow;
    }


    public int getFirstColumn() {
        return firstCol;
    }


    public void setFirstColumn(int firstCol) {
        this.firstCol = firstCol;
    }


    public int getLastRow() {
        return lastRow;
    }


    public void setLastRow(int lastRow) {
        this.lastRow = lastRow;
    }


    public int getLastColumn() {
        return lastCol;
    }


    public void setLastColumn(int lastCol) {
        this.lastCol = lastCol;
    }


    public boolean isInRange(int rowInd, int colInd) {
        return firstRow <= rowInd && rowInd <= lastRow &&
                firstCol <= colInd && colInd <= lastCol;
    }

    public void dispose() {

    }

}
