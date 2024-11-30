
package com.document.render.office.ss.model.table;



public class SSTableStyle {

    private SSTableCellStyle band1H;
    private SSTableCellStyle band2H;
    private SSTableCellStyle band1V;
    private SSTableCellStyle band2V;
    private SSTableCellStyle firstCol;
    private SSTableCellStyle lastCol;
    private SSTableCellStyle firstRow;
    private SSTableCellStyle lastRow;

    public SSTableCellStyle getBand1H() {
        return band1H;
    }

    public void setBand1H(SSTableCellStyle band1h) {
        band1H = band1h;
    }

    public SSTableCellStyle getBand2H() {
        return band2H;
    }

    public void setBand2H(SSTableCellStyle band2h) {
        band2H = band2h;
    }

    public SSTableCellStyle getBand1V() {
        return band1V;
    }

    public void setBand1V(SSTableCellStyle band1v) {
        band1V = band1v;
    }

    public SSTableCellStyle getBand2V() {
        return band2V;
    }

    public void setBand2V(SSTableCellStyle band2v) {
        band2V = band2v;
    }

    public SSTableCellStyle getFirstCol() {
        return firstCol;
    }

    public void setFirstCol(SSTableCellStyle firstCol) {
        this.firstCol = firstCol;
    }

    public SSTableCellStyle getLastCol() {
        return lastCol;
    }

    public void setLastCol(SSTableCellStyle lastCol) {
        this.lastCol = lastCol;
    }

    public SSTableCellStyle getFirstRow() {
        return firstRow;
    }

    public void setFirstRow(SSTableCellStyle firstRow) {
        this.firstRow = firstRow;
    }

    public SSTableCellStyle getLastRow() {
        return lastRow;
    }

    public void setLastRow(SSTableCellStyle lastRow) {
        this.lastRow = lastRow;
    }
}
