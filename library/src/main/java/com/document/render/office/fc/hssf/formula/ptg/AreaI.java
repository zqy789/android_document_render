

package com.document.render.office.fc.hssf.formula.ptg;


public interface AreaI {

    public int getFirstRow();


    public int getLastRow();


    public int getFirstColumn();


    public int getLastColumn();

    class OffsetArea implements AreaI {

        private final int _firstColumn;
        private final int _firstRow;
        private final int _lastColumn;
        private final int _lastRow;

        public OffsetArea(int baseRow, int baseColumn, int relFirstRowIx, int relLastRowIx,
                          int relFirstColIx, int relLastColIx) {
            _firstRow = baseRow + Math.min(relFirstRowIx, relLastRowIx);
            _lastRow = baseRow + Math.max(relFirstRowIx, relLastRowIx);
            _firstColumn = baseColumn + Math.min(relFirstColIx, relLastColIx);
            _lastColumn = baseColumn + Math.max(relFirstColIx, relLastColIx);
        }

        public int getFirstColumn() {
            return _firstColumn;
        }

        public int getFirstRow() {
            return _firstRow;
        }

        public int getLastColumn() {
            return _lastColumn;
        }

        public int getLastRow() {
            return _lastRow;
        }
    }
}
