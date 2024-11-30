
package com.document.render.office.pg.model.tableStyle;


public class TableStyle {

    private TableCellStyle wholeTable;
    private TableCellStyle band1H;
    private TableCellStyle band2H;
    private TableCellStyle band1V;
    private TableCellStyle band2V;
    private TableCellStyle firstCol;
    private TableCellStyle lastCol;
    private TableCellStyle firstRow;
    private TableCellStyle lastRow;

    public TableCellStyle getWholeTable() {
        return wholeTable;
    }

    public void setWholeTable(TableCellStyle wholeTable) {
        this.wholeTable = wholeTable;
    }

    public TableCellStyle getBand1H() {
        return band1H;
    }

    public void setBand1H(TableCellStyle band1h) {
        band1H = band1h;
    }

    public TableCellStyle getBand2H() {
        return band2H;
    }

    public void setBand2H(TableCellStyle band2h) {
        band2H = band2h;
    }

    public TableCellStyle getBand1V() {
        return band1V;
    }

    public void setBand1V(TableCellStyle band1v) {
        band1V = band1v;
    }

    public TableCellStyle getBand2V() {
        return band2V;
    }

    public void setBand2V(TableCellStyle band2v) {
        band2V = band2v;
    }

    public TableCellStyle getFirstCol() {
        return firstCol;
    }

    public void setFirstCol(TableCellStyle firstCol) {
        this.firstCol = firstCol;
    }

    public TableCellStyle getLastCol() {
        return lastCol;
    }

    public void setLastCol(TableCellStyle lastCol) {
        this.lastCol = lastCol;
    }

    public TableCellStyle getFirstRow() {
        return firstRow;
    }

    public void setFirstRow(TableCellStyle firstRow) {
        this.firstRow = firstRow;
    }

    public TableCellStyle getLastRow() {
        return lastRow;
    }

    public void setLastRow(TableCellStyle lastRow) {
        this.lastRow = lastRow;
    }

    public void dispose() {
        if (wholeTable != null) {
            wholeTable.dispose();
            wholeTable = null;
        }

        if (band1H != null) {
            band1H.dispose();
            band1H = null;
        }

        if (band2H != null) {
            band2H.dispose();
            band2H = null;
        }

        if (band1V != null) {
            band1V.dispose();
            band1V = null;
        }

        if (band2V != null) {
            band2V.dispose();
            band2V = null;
        }

        if (firstCol != null) {
            firstCol.dispose();
            firstCol = null;
        }

        if (lastCol != null) {
            lastCol.dispose();
            lastCol = null;
        }

        if (firstRow != null) {
            firstRow.dispose();
            firstRow = null;
        }

        if (lastRow != null) {
            lastRow.dispose();
            lastRow = null;
        }
    }
}
