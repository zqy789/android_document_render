
package com.document.render.office.ss.other;

import com.document.render.office.ss.model.CellRangeAddress;
import com.document.render.office.ss.model.baseModel.Cell;


public class ExpandedCellRangeAddress {
    private CellRangeAddress rangeAddr;
    private Cell expandedCell;

    public ExpandedCellRangeAddress(Cell expandedCell, int firstRow, int firstCol, int lastRow, int lastCol) {
        this.expandedCell = expandedCell;
        rangeAddr = new CellRangeAddress(firstRow, firstCol, lastRow, lastCol);
    }


    public CellRangeAddress getRangedAddress() {
        return rangeAddr;
    }


    public Cell getExpandedCell() {
        return expandedCell;
    }

    public void dispose() {
        rangeAddr = null;

        expandedCell = null;

    }
}
