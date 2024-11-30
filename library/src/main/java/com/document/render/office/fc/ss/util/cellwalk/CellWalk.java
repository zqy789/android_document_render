

package com.document.render.office.fc.ss.util.cellwalk;


import com.document.render.office.fc.ss.usermodel.ICell;
import com.document.render.office.fc.ss.usermodel.IRow;
import com.document.render.office.fc.ss.usermodel.Sheet;
import com.document.render.office.fc.ss.util.DataMarker;
import com.document.render.office.fc.ss.util.HSSFCellRangeAddress;



public class CellWalk {

    private Sheet sheet;
    private HSSFCellRangeAddress range;
    private boolean traverseEmptyCells;

    public CellWalk(DataMarker dm) {
        this(dm.getSheet(), dm.getRange());
    }

    public CellWalk(Sheet sheet, HSSFCellRangeAddress range) {
        this.sheet = sheet;
        this.range = range;
        this.traverseEmptyCells = false;
    }


    public boolean isTraverseEmptyCells() {
        return traverseEmptyCells;
    }


    public void setTraverseEmptyCells(boolean traverseEmptyCells) {
        this.traverseEmptyCells = traverseEmptyCells;
    }


    public void traverse(CellHandler handler) {
        int firstRow = range.getFirstRow();
        int lastRow = range.getLastRow();
        int firstColumn = range.getFirstColumn();
        int lastColumn = range.getLastColumn();
        final int width = lastColumn - firstColumn + 1;
        SimpleCellWalkContext ctx = new SimpleCellWalkContext();
        IRow currentRow = null;
        ICell currentCell = null;























    }

    private boolean isEmpty(ICell cell) {
        return (cell.getCellType() == ICell.CELL_TYPE_BLANK);
    }


    private class SimpleCellWalkContext implements CellWalkContext {
        public long ordinalNumber = 0;
        public int rowNumber = 0;
        public int colNumber = 0;

        public long getOrdinalNumber() {
            return ordinalNumber;
        }

        public int getRowNumber() {
            return rowNumber;
        }

        public int getColumnNumber() {
            return colNumber;
        }
    }
}
