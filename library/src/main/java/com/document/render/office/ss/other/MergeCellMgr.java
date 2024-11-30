
package com.document.render.office.ss.other;

import com.document.render.office.ss.model.CellRangeAddress;
import com.document.render.office.ss.model.baseModel.Sheet;
import com.document.render.office.ss.model.sheetProperty.PaneInformation;
import com.document.render.office.ss.view.SheetView;


public class MergeCellMgr {
    
    private CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 0, 0);
    private MergeCell mergedCell = new MergeCell();

    
    public CellRangeAddress getVisibleCellRangeAddress(Sheet sheet, CellRangeAddress cr) {
        int firstCol = cr.getFirstColumn();
        int lastCol = cr.getLastColumn();
        int firstRow = cr.getFirstRow();
        int lastRow = cr.getLastRow();


        
        while (firstCol <= lastCol && sheet.isColumnHidden(firstCol)) {
            firstCol++;
        }

        while (lastCol >= firstCol && sheet.isColumnHidden(lastCol)) {
            lastCol--;
        }

        while (firstRow <= lastRow && sheet.getRow(firstRow).isZeroHeight()) {
            firstRow++;
        }
        while (lastRow >= firstRow && sheet.getRow(lastRow).isZeroHeight()) {
            lastRow--;
        }

        cellRangeAddress.setFirstColumn(firstCol);
        cellRangeAddress.setFirstRow(firstRow);
        cellRangeAddress.setLastColumn(lastCol);
        cellRangeAddress.setLastRow(lastRow);

        return cellRangeAddress;
    }

    
    public boolean isDrawMergeCell(SheetView sheetView, CellRangeAddress cr, int row, int col) {
        boolean isDraw = false;
        int minColumn = sheetView.getMinRowAndColumnInformation().getMinColumnIndex();
        int minRow = sheetView.getMinRowAndColumnInformation().getMinRowIndex();
        PaneInformation paneInfo = sheetView.getCurrentSheet().getPaneInformation();
        
        getVisibleCellRangeAddress(sheetView.getCurrentSheet(), cr);

        if (paneInfo != null) {
            if (row < paneInfo.getHorizontalSplitTopRow() && cr.getLastRow() >= paneInfo.getHorizontalSplitTopRow()) {
                cellRangeAddress.setLastRow(paneInfo.getHorizontalSplitTopRow() - 1);
                minRow = 0;
            } else if (row >= paneInfo.getHorizontalSplitTopRow() && cr.getFirstRow() < paneInfo.getHorizontalSplitTopRow()) {
                cellRangeAddress.setFirstRow(paneInfo.getHorizontalSplitTopRow());
            }

            if (col < paneInfo.getVerticalSplitLeftColumn() && cr.getLastColumn() >= paneInfo.getVerticalSplitLeftColumn()) {
                cellRangeAddress.setLastColumn(paneInfo.getVerticalSplitLeftColumn() - 1);
                minColumn = 0;
            } else if (col >= paneInfo.getVerticalSplitLeftColumn() && cr.getFirstColumn() < paneInfo.getVerticalSplitLeftColumn()) {
                cellRangeAddress.setFirstColumn(paneInfo.getVerticalSplitLeftColumn());
            }
        }

        
        if (cellRangeAddress.getFirstColumn() == col && cellRangeAddress.getFirstRow() == row) {
            isDraw = true;
        }
        
        else if (row == cellRangeAddress.getFirstRow() && col > cellRangeAddress.getFirstColumn()) {
            isDraw = col == minColumn;
        }
        
        else if (col == cellRangeAddress.getFirstColumn() && row > cellRangeAddress.getFirstRow()) {
            isDraw = row == minRow;
        }
        
        else if (row > cellRangeAddress.getFirstRow() && col > cellRangeAddress.getFirstColumn()) {
            isDraw = col == minColumn && row == minRow;
        }
        return isDraw;
    }

    
    public MergeCell getMergedCellSize(SheetView sheetView, CellRangeAddress cellRangeAddress, int row, int col) {
        
        mergedCell.reset();

        int minColumn = sheetView.getMinRowAndColumnInformation().getMinColumnIndex();
        int minRow = sheetView.getMinRowAndColumnInformation().getMinRowIndex();
        PaneInformation paneInfo = sheetView.getCurrentSheet().getPaneInformation();

        if (paneInfo == null) {
            for (int i = cellRangeAddress.getFirstColumn(); i <= cellRangeAddress.getLastColumn(); i++) {
                if (!sheetView.getCurrentSheet().isColumnHidden(i)) {
                    float tW = (sheetView.getCurrentSheet().getColumnPixelWidth(i) * sheetView.getZoom());
                    mergedCell.setWidth(mergedCell.getWidth() + tW);
                    if (i < minColumn) {
                        mergedCell.setNovisibleWidth(mergedCell.getNovisibleWidth() + tW);
                    }
                }
            }
            for (int i = cellRangeAddress.getFirstRow(); i <= cellRangeAddress.getLastRow(); i++) {
                if (!sheetView.getCurrentSheet().getRow(i).isZeroHeight()) {
                    float tH = (sheetView.getCurrentSheet().getRow(i).getRowPixelHeight() * sheetView.getZoom());
                    mergedCell.setHeight(mergedCell.getHeight() + tH);
                    if (i < minRow) {
                        mergedCell.setNoVisibleHeight(mergedCell.getNoVisibleHeight() + tH);
                    }
                }
            }
        } else {
            
            if (col >= paneInfo.getVerticalSplitLeftColumn()) {
                
                for (int i = cellRangeAddress.getFirstColumn(); i <= cellRangeAddress.getLastColumn(); i++) {
                    if (!sheetView.getCurrentSheet().isColumnHidden(i)) {
                        float tW = (sheetView.getCurrentSheet().getColumnPixelWidth(i) * sheetView.getZoom());
                        mergedCell.setWidth(mergedCell.getWidth() + tW);
                        if (i < minColumn) {
                            mergedCell.setNovisibleWidth(mergedCell.getNovisibleWidth() + tW);
                        }
                    }
                }
            } else {
                
                mergedCell.setFrozenColumn(true);
                for (int i = cellRangeAddress.getFirstColumn(); i <= cellRangeAddress.getLastColumn(); i++) {
                    if (!sheetView.getCurrentSheet().isColumnHidden(i)) {
                        float tW = (sheetView.getCurrentSheet().getColumnPixelWidth(i) * sheetView.getZoom());
                        mergedCell.setWidth(mergedCell.getWidth() + tW);
                        if (i >= paneInfo.getVerticalSplitLeftColumn()) {
                            mergedCell.setNovisibleWidth(mergedCell.getNovisibleWidth() + tW);
                        }
                    }
                }
            }

            
            if (row >= paneInfo.getHorizontalSplitTopRow()) {
                
                for (int i = cellRangeAddress.getFirstRow(); i <= cellRangeAddress.getLastRow(); i++) {
                    if (!sheetView.getCurrentSheet().getRow(i).isZeroHeight()) {
                        float tH = (sheetView.getCurrentSheet().getRow(i).getRowPixelHeight() * sheetView.getZoom());
                        mergedCell.setHeight(mergedCell.getHeight() + tH);
                        if (i < minRow) {
                            mergedCell.setNoVisibleHeight(mergedCell.getNoVisibleHeight() + tH);
                        }
                    }
                }
            } else {
                
                mergedCell.setFrozenRow(true);
                for (int i = cellRangeAddress.getFirstRow(); i <= cellRangeAddress.getLastRow(); i++) {
                    if (!sheetView.getCurrentSheet().getRow(i).isZeroHeight()) {
                        float tH = (sheetView.getCurrentSheet().getRow(i).getRowPixelHeight() * sheetView.getZoom());
                        mergedCell.setHeight(mergedCell.getHeight() + tH);
                        if (i >= paneInfo.getHorizontalSplitTopRow()) {
                            mergedCell.setNoVisibleHeight(mergedCell.getNoVisibleHeight() + tH);
                        }
                    }
                }
            }
        }

        return mergedCell;
    }

    public void dispose() {
        if (cellRangeAddress != null) {
            cellRangeAddress.dispose();
            cellRangeAddress = null;
        }

        if (mergedCell != null) {
            mergedCell.dispose();
            mergedCell = null;
        }

    }
}
