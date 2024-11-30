

package com.document.render.office.fc.ss.util;



public class Region implements Comparable<Region> {
    private int _rowFrom;
    private short _colFrom;
    private int _rowTo;
    private short _colTo;

    
    public Region() {
    }

    public Region(int rowFrom, short colFrom, int rowTo, short colTo) {
        this._rowFrom = rowFrom;
        this._rowTo = rowTo;
        this._colFrom = colFrom;
        this._colTo = colTo;
    }

    public Region(String ref) {
        CellReference cellReferenceFrom = new CellReference(ref.substring(0, ref.indexOf(":")));
        CellReference cellReferenceTo = new CellReference(ref.substring(ref.indexOf(":") + 1));
        this._rowFrom = cellReferenceFrom.getRow();
        this._colFrom = cellReferenceFrom.getCol();
        this._rowTo = cellReferenceTo.getRow();
        this._colTo = cellReferenceTo.getCol();
    }

    
    public static Region[] convertCellRangesToRegions(HSSFCellRangeAddress[] cellRanges) {
        int size = cellRanges.length;
        if (size < 1) {
            return new Region[0];
        }

        Region[] result = new Region[size];

        for (int i = 0; i != size; i++) {
            result[i] = convertToRegion(cellRanges[i]);
        }
        return result;
    }

    private static Region convertToRegion(HSSFCellRangeAddress cr) {

        return new Region(cr.getFirstRow(), (short) cr.getFirstColumn(), cr.getLastRow(), (short) cr.getLastColumn());
    }

    public static HSSFCellRangeAddress[] convertRegionsToCellRanges(Region[] regions) {
        int size = regions.length;
        if (size < 1) {
            return new HSSFCellRangeAddress[0];
        }

        HSSFCellRangeAddress[] result = new HSSFCellRangeAddress[size];

        for (int i = 0; i != size; i++) {
            result[i] = convertToCellRangeAddress(regions[i]);
        }
        return result;
    }

    public static HSSFCellRangeAddress convertToCellRangeAddress(Region r) {
        return new HSSFCellRangeAddress(r.getRowFrom(), r.getRowTo(), r.getColumnFrom(), r.getColumnTo());
    }

    
    public short getColumnFrom() {
        return _colFrom;
    }

    
    public void setColumnFrom(short colFrom) {
        this._colFrom = colFrom;
    }

    
    public int getRowFrom() {
        return _rowFrom;
    }

    
    public void setRowFrom(int rowFrom) {
        this._rowFrom = rowFrom;
    }

    

    public short getColumnTo() {
        return _colTo;
    }

    

    public void setColumnTo(short colTo) {
        this._colTo = colTo;
    }

    
    public int getRowTo() {
        return _rowTo;
    }

    
    public void setRowTo(int rowTo) {
        this._rowTo = rowTo;
    }

    
    public boolean contains(int row, short col) {
        if ((this._rowFrom <= row) && (this._rowTo >= row)
                && (this._colFrom <= col) && (this._colTo >= col)) {



            return true;
        }
        return false;
    }

    public boolean equals(Region r) {
        return (compareTo(r) == 0);
    }

    
    public int compareTo(Region r) {
        if ((this.getRowFrom() == r.getRowFrom())
                && (this.getColumnFrom() == r.getColumnFrom())
                && (this.getRowTo() == r.getRowTo())
                && (this.getColumnTo() == r.getColumnTo())) {
            return 0;
        }
        if ((this.getRowFrom() < r.getRowFrom())
                || (this.getColumnFrom() < r.getColumnFrom())
                || (this.getRowTo() < r.getRowTo())
                || (this.getColumnTo() < r.getColumnTo())) {
            return 1;
        }
        return -1;
    }

    
    public int getArea() {
        return (_rowTo - _rowFrom + 1) * (_colTo - _colFrom + 1);
    }

    
    public String getRegionRef() {
        CellReference cellRefFrom = new CellReference(_rowFrom, _colFrom);
        CellReference cellRefTo = new CellReference(_rowTo, _colTo);
        String ref = cellRefFrom.formatAsString() + ":" + cellRefTo.formatAsString();
        return ref;
    }
}
