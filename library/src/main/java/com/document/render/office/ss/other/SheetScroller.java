
package com.document.render.office.ss.other;

import com.document.render.office.ss.model.baseModel.Row;
import com.document.render.office.ss.model.baseModel.Sheet;
import com.document.render.office.ss.model.baseModel.Workbook;
import com.document.render.office.ss.model.sheetProperty.PaneInformation;



public class SheetScroller {

    private int minRowIndex;
    private int minColumnIndex;

    private float columnWidth;
    private float rowHeight;
    private boolean isRowAllVisible = true;
    private boolean isColumnAllVisible = true;

    private double visibleRowHeight;
    private double visibleColumnWidth;

    public void reset() {
        setMinRowIndex(0);
        setMinColumnIndex(0);

        setRowHeight(0);
        setColumnWidth(0);

        setVisibleRowHeight(0);
        setVisibleColumnWidth(0);

        setRowAllVisible(true);
        setColumnAllVisible(true);
    }

    public void update(Sheet sheet, int scrollX, int scrollY) {
        reset();

        setVisibleRowHeight(scrollY);
        setVisibleColumnWidth(scrollX);

        PaneInformation paneInfo = sheet.getPaneInformation();
        if (paneInfo != null) {
            setMinRowIndex(paneInfo.getHorizontalSplitTopRow());
            setMinColumnIndex(paneInfo.getVerticalSplitLeftColumn());
        }

        int maxSheetRows = sheet.getWorkbook().isBefore07Version() ? Workbook.MAXROW_03 : Workbook.MAXROW_07;
        int maxSheetColumns = sheet.getWorkbook().isBefore07Version() ? Workbook.MAXCOLUMN_03 : Workbook.MAXCOLUMN_07;
        Row row;
        if (scrollY > 0) {
            int firstRow = sheet.getFirstRowNum();
            int lastRow = sheet.getLastRowNum();
            int defaultRowHeight = sheet.getDefaultRowHeight();


            while (visibleRowHeight >= 1 && minRowIndex <= maxSheetRows) {
                if (minRowIndex >= firstRow && minRowIndex <= lastRow) {
                    row = sheet.getRow(minRowIndex);
                } else {
                    row = null;
                }

                if (row == null || (row != null && !row.isZeroHeight())) {
                    rowHeight = (row == null ? defaultRowHeight : row.getRowPixelHeight());
                    visibleRowHeight = visibleRowHeight - rowHeight;
                }
                minRowIndex++;
            }

            if (minRowIndex != maxSheetRows) {
                minRowIndex--;
                setVisibleRowHeight(Math.abs(getVisibleRowHeight()));
                if (getVisibleRowHeight() < 1) {
                    minRowIndex++;
                    setVisibleRowHeight(0);
                } else {
                    setRowAllVisible(false);
                }
            } else {
                minRowIndex--;
                row = sheet.getRow(minRowIndex);
                while (row != null && row.isZeroHeight()) {
                    minRowIndex--;
                    row = sheet.getRow(getMinRowIndex());
                }

                setVisibleRowHeight(0);
            }
        }

        if (scrollX > 0) {

            while (visibleColumnWidth >= 1 && minColumnIndex <= maxSheetColumns) {
                if (!sheet.isColumnHidden(minColumnIndex)) {
                    columnWidth = sheet.getColumnPixelWidth(minColumnIndex);
                    visibleColumnWidth -= columnWidth;
                }
                minColumnIndex++;
            }

            if (minColumnIndex != maxSheetColumns) {
                minColumnIndex--;
                setVisibleColumnWidth(Math.abs(getVisibleColumnWidth()));
                if (getVisibleColumnWidth() < 1) {
                    minColumnIndex++;
                    setVisibleColumnWidth(0);
                } else {
                    setColumnAllVisible(false);
                }
            } else {
                minColumnIndex--;
                while (sheet.isColumnHidden(minColumnIndex)) {
                    minColumnIndex--;
                }
                setVisibleColumnWidth(0);
            }
        }
    }


    public int getMinRowIndex() {
        return minRowIndex;
    }


    public void setMinRowIndex(int minRowIndex) {
        this.minRowIndex = minRowIndex;
    }


    public void dispose() {

    }


    public int getMinColumnIndex() {
        return minColumnIndex;
    }


    public void setMinColumnIndex(int minColumnIndex) {
        this.minColumnIndex = minColumnIndex;
    }


    public float getColumnWidth() {
        return columnWidth;
    }


    public void setColumnWidth(float columnWidth) {
        this.columnWidth = columnWidth;
    }


    public float getRowHeight() {
        return rowHeight;
    }


    public void setRowHeight(float rowHeight) {
        this.rowHeight = rowHeight;
    }


    public boolean isRowAllVisible() {
        return isRowAllVisible;
    }


    public void setRowAllVisible(boolean isRowAllVisible) {
        this.isRowAllVisible = isRowAllVisible;
    }


    public boolean isColumnAllVisible() {
        return isColumnAllVisible;
    }


    public void setColumnAllVisible(boolean isColumnAllVisible) {
        this.isColumnAllVisible = isColumnAllVisible;
    }


    public double getVisibleRowHeight() {
        return visibleRowHeight;
    }


    public void setVisibleRowHeight(double visibleRowHeight) {
        this.visibleRowHeight = visibleRowHeight;
    }


    public double getVisibleColumnWidth() {
        return visibleColumnWidth;
    }


    public void setVisibleColumnWidth(double visibleColumnWidth) {
        this.visibleColumnWidth = visibleColumnWidth;
    }
}
