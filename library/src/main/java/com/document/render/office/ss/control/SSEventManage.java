

package com.document.render.office.ss.control;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.document.render.office.constant.EventConstant;
import com.document.render.office.ss.model.CellRangeAddress;
import com.document.render.office.ss.model.baseModel.Cell;
import com.document.render.office.ss.model.baseModel.Row;
import com.document.render.office.ss.model.baseModel.Sheet;
import com.document.render.office.ss.model.baseModel.Workbook;
import com.document.render.office.ss.other.DrawingCell;
import com.document.render.office.ss.other.FocusCell;
import com.document.render.office.ss.view.SheetView;
import com.document.render.office.system.IControl;
import com.document.render.office.system.ITimerListener;
import com.document.render.office.system.beans.AEventManage;
import com.document.render.office.system.beans.ATimer;


public class SSEventManage extends AEventManage implements ITimerListener {
    private static final int MINDISTANCE = 10;
    private boolean longPress;

    private int oldX;

    private int oldY;

    private Spreadsheet spreadsheet;

    private FocusCell oldHeaderArea;
    private FocusCell newHeaderArea;

    private boolean actionDown;

    private boolean scrolling;

    private ATimer timer;


    public SSEventManage(Spreadsheet spreadsheet, IControl control) {
        super(spreadsheet.getContext(), control);
        this.spreadsheet = spreadsheet;
        this.timer = new ATimer(1000, this);
    }


    public void actionPerformed() {
        timer.stop();
        control.actionEvent(EventConstant.APP_GENERATED_PICTURE_ID, null);
    }


    private void changingHeader(MotionEvent event) {

        if (newHeaderArea == null) {
            return;
        }

        scrolling = true;

        float x = event.getX();
        float y = event.getY();

        Rect area;
        switch (newHeaderArea.getType()) {
            case FocusCell.ROWHEADER:
                area = newHeaderArea.getRect();
                area.bottom = Math.round(oldHeaderArea.getRect().bottom + (y - oldY));
                if (area.bottom <= area.top + MINDISTANCE) {
                    area.bottom = area.top + MINDISTANCE;
                }
                break;
            case FocusCell.COLUMNHEADER:
                area = newHeaderArea.getRect();
                area.right = Math.round(oldHeaderArea.getRect().right + (x - oldX));
                if (area.right <= area.left + MINDISTANCE) {
                    area.right = area.left + MINDISTANCE;
                }
                break;
            default:
                break;
        }

        spreadsheet.getSheetView().changeHeaderArea(newHeaderArea);

    }


    private boolean checkClickedCell(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (spreadsheet.getSheetView().getColumnHeaderHeight() > y
                || spreadsheet.getSheetView().getRowHeaderWidth() > x) {

            return false;
        }

        SheetView sheetView = spreadsheet.getSheetView();
        DrawingCell cellInfor = new DrawingCell();

        cellInfor.setLeft(sheetView.getRowHeaderWidth());
        cellInfor.setTop(sheetView.getColumnHeaderHeight());
        cellInfor.setRowIndex(sheetView.getMinRowAndColumnInformation().getMinRowIndex());
        cellInfor.setColumnIndex(sheetView.getMinRowAndColumnInformation().getMinColumnIndex());


        int maxRows = sheetView.getCurrentSheet().getWorkbook().isBefore07Version() ? Workbook.MAXROW_03 : Workbook.MAXROW_07;
        while (cellInfor.getTop() <= y && cellInfor.getRowIndex() <= maxRows) {
            Row row = sheetView.getCurrentSheet().getRow(cellInfor.getRowIndex());
            if (row != null && row.isZeroHeight()) {
                cellInfor.setRowIndex(cellInfor.getRowIndex() + 1);
                continue;
            }

            cellInfor.setHeight(row == null ? sheetView.getCurrentSheet().getDefaultRowHeight() : row.getRowPixelHeight());
            cellInfor.setHeight(Math.round(cellInfor.getHeight() * sheetView.getZoom()));

            if (cellInfor.getRowIndex() == sheetView.getMinRowAndColumnInformation().getMinRowIndex()
                    && !sheetView.getMinRowAndColumnInformation().isRowAllVisible()) {
                cellInfor.setVisibleHeight(
                        Math.round(
                                sheetView.getMinRowAndColumnInformation().getVisibleRowHeight()
                                        * sheetView.getZoom()));
            } else {
                cellInfor.setVisibleHeight(cellInfor.getHeight());
            }


            cellInfor.setTop(cellInfor.getTop() + cellInfor.getVisibleHeight());
            cellInfor.setRowIndex(cellInfor.getRowIndex() + 1);
        }


        int maxColumns = sheetView.getCurrentSheet().getWorkbook().isBefore07Version() ? Workbook.MAXCOLUMN_03 : Workbook.MAXCOLUMN_07;
        while (cellInfor.getLeft() <= x && cellInfor.getColumnIndex() <= maxColumns) {
            if (sheetView.getCurrentSheet().isColumnHidden(cellInfor.getColumnIndex())) {
                cellInfor.setColumnIndex(cellInfor.getColumnIndex() + 1);
                continue;
            }
            cellInfor.setWidth(Math.round(sheetView.getCurrentSheet().getColumnPixelWidth(cellInfor.getColumnIndex()) * sheetView.getZoom()));



            if (cellInfor.getColumnIndex() == sheetView.getMinRowAndColumnInformation().getMinColumnIndex()
                    && !sheetView.getMinRowAndColumnInformation().isColumnAllVisible()) {
                cellInfor.setVisibleWidth(Math.round(sheetView.getMinRowAndColumnInformation().getVisibleColumnWidth() * sheetView.getZoom()));
            } else {
                cellInfor.setVisibleWidth(cellInfor.getWidth());

            }




            cellInfor.setLeft(cellInfor.getLeft() + cellInfor.getVisibleWidth());
            cellInfor.setColumnIndex(cellInfor.getColumnIndex() + 1);
        }

        spreadsheet.getSheetView().getCurrentSheet().setActiveCellType(Sheet.ACTIVECELL_SINGLE);
        spreadsheet.getSheetView().selectedCell(cellInfor.getRowIndex() - 1, cellInfor.getColumnIndex() - 1);


        spreadsheet.getControl().actionEvent(EventConstant.APP_CONTENT_SELECTED, null);

        spreadsheet.abortDrawing();
        spreadsheet.postInvalidate();
        return true;
    }


    private boolean changeHeaderEnd() {
        boolean ret = false;

        scrolling = false;

        if (oldHeaderArea != null) {
            float off;
            Sheet sheet = spreadsheet.getSheetView().getCurrentSheet();
            Row row;
            Cell cell;
            int index;
            switch (oldHeaderArea.getType()) {
                case FocusCell.ROWHEADER:
                    ret = true;
                    index = newHeaderArea.getRow();
                    row = sheet.getRow(index);
                    if (row == null) {
                        row = new Row(0);
                        row.setRowNumber(index);
                        row.setSheet(sheet);
                        sheet.addRow(row);
                    } else {

                        while (sheet.getRow(index) != null && sheet.getRow(index).isZeroHeight()) {
                            index--;
                        }
                        row = sheet.getRow(index);
                        if (row == null) {
                            row = new Row(0);
                            row.setRowNumber(index);
                            row.setSheet(sheet);
                            sheet.addRow(row);
                        }
                    }


                {
                    off = (newHeaderArea.getRect().bottom - newHeaderArea.getRect().top) - (float) (oldHeaderArea.getRect().bottom - oldHeaderArea.getRect().top);

                    row.setRowPixelHeight(Math.round(row.getRowPixelHeight() + off / spreadsheet.getSheetView().getZoom()));


                    index = row.getRowNumber();
                    while (index <= sheet.getLastRowNum()) {
                        row = sheet.getRow(index++);
                        if (row == null) {
                            continue;
                        }
                        for (int i = row.getFirstCol(); i <= row.getLastCol(); i++) {
                            if ((cell = row.getCell(i)) != null) {
                                if (cell.getRangeAddressIndex() >= 0) {

                                    CellRangeAddress cr = sheet.getMergeRange(cell.getRangeAddressIndex());
                                    Row tRow = sheet.getRow(cr.getFirstRow());
                                    cell = tRow.getCell(cr.getFirstColumn());
                                }
                                cell.removeSTRoot();
                            }
                        }

                        row.setInitExpandedRangeAddress(false);
                    }
                }
                break;
                case FocusCell.COLUMNHEADER:
                    ret = true;
                    off = (newHeaderArea.getRect().right - newHeaderArea.getRect().left) - (oldHeaderArea.getRect().right - oldHeaderArea.getRect().left);
                    index = newHeaderArea.getColumn();
                    while (sheet.isColumnHidden(index)) {
                        index--;
                    }
                    int width = Math.round(sheet.getColumnPixelWidth(index) + off / spreadsheet.getSheetView().getZoom());
                    sheet.setColumnPixelWidth(index, width);


                    index = sheet.getFirstRowNum();
                    while (index <= sheet.getLastRowNum()) {
                        row = sheet.getRow(index++);
                        if (row == null) {
                            continue;
                        }

                        for (int i = Math.max(row.getFirstCol(), oldHeaderArea.getColumn()); i <= row.getLastCol(); i++) {
                            if ((cell = row.getCell(i)) != null) {
                                if (cell.getRangeAddressIndex() >= 0) {

                                    CellRangeAddress cr = sheet.getMergeRange(cell.getRangeAddressIndex());
                                    Row tRow = sheet.getRow(cr.getFirstRow());
                                    cell = tRow.getCell(cr.getFirstColumn());
                                }
                                cell.removeSTRoot();
                            }
                        }
                        row.setInitExpandedRangeAddress(false);
                    }

                    break;
                default:
                    break;
            }

            spreadsheet.getSheetView().updateMinRowAndColumnInfo();

            spreadsheet.getSheetView().setDrawMovingHeaderLine(false);

            oldHeaderArea = null;
            newHeaderArea = null;
        }

        return ret;
    }





























    private int findClickedRowHeader(MotionEvent event) {
        float y = event.getY();

        SheetView sheetView = spreadsheet.getSheetView();
        DrawingCell cellInfor = new DrawingCell();

        cellInfor.setTop(sheetView.getColumnHeaderHeight());
        cellInfor.setRowIndex(sheetView.getMinRowAndColumnInformation().getMinRowIndex());


        int maxRows = sheetView.getCurrentSheet().getWorkbook().isBefore07Version() ? Workbook.MAXROW_03 : Workbook.MAXROW_07;
        while (cellInfor.getTop() <= y && cellInfor.getRowIndex() <= maxRows) {
            Row row = sheetView.getCurrentSheet().getRow(cellInfor.getRowIndex());
            if (row != null && row.isZeroHeight()) {
                cellInfor.setRowIndex(cellInfor.getRowIndex() + 1);
                continue;
            }

            cellInfor.setHeight(row == null ? sheetView.getCurrentSheet().getDefaultRowHeight() : row.getRowPixelHeight());
            cellInfor.setHeight(Math.round(cellInfor.getHeight() * sheetView.getZoom()));

            if (cellInfor.getRowIndex() == sheetView.getMinRowAndColumnInformation().getMinRowIndex()
                    && !sheetView.getMinRowAndColumnInformation().isRowAllVisible()) {
                cellInfor.setVisibleHeight(
                        Math.round(
                                sheetView.getMinRowAndColumnInformation().getVisibleRowHeight()
                                        * sheetView.getZoom()));
            } else {
                cellInfor.setVisibleHeight(cellInfor.getHeight());
            }


            cellInfor.setTop(cellInfor.getTop() + cellInfor.getVisibleHeight());
            cellInfor.setRowIndex(cellInfor.getRowIndex() + 1);
        }


        return cellInfor.getRowIndex() - 1;
    }


    private int findClickedColumnHeader(MotionEvent event) {
        float x = event.getX();

        SheetView sheetView = spreadsheet.getSheetView();
        DrawingCell cellInfor = new DrawingCell();

        cellInfor.setLeft(sheetView.getRowHeaderWidth());
        cellInfor.setColumnIndex(sheetView.getMinRowAndColumnInformation().getMinColumnIndex());


        int maxColumns = sheetView.getCurrentSheet().getWorkbook().isBefore07Version() ? Workbook.MAXCOLUMN_03 : Workbook.MAXCOLUMN_07;
        while (cellInfor.getLeft() <= x && cellInfor.getColumnIndex() <= maxColumns) {
            if (sheetView.getCurrentSheet().isColumnHidden(cellInfor.getColumnIndex())) {
                cellInfor.setColumnIndex(cellInfor.getColumnIndex() + 1);
                continue;
            }
            cellInfor.setWidth(Math.round(sheetView.getCurrentSheet().getColumnPixelWidth(cellInfor.getColumnIndex()) * sheetView.getZoom()));



            if (cellInfor.getColumnIndex() == sheetView.getMinRowAndColumnInformation().getMinColumnIndex()
                    && !sheetView.getMinRowAndColumnInformation().isColumnAllVisible()) {
                cellInfor.setVisibleWidth(Math.round(sheetView.getMinRowAndColumnInformation().getVisibleColumnWidth() * sheetView.getZoom()));
            } else {
                cellInfor.setVisibleWidth(cellInfor.getWidth());

            }

            cellInfor.setLeft(cellInfor.getLeft() + cellInfor.getVisibleWidth());
            cellInfor.setColumnIndex(cellInfor.getColumnIndex() + 1);
        }

        return cellInfor.getColumnIndex() - 1;

    }


    private boolean checkClickedHeader(MotionEvent event) {
        boolean ret = false;
        float x = event.getX();
        float y = event.getY();

        SheetView sheetView = spreadsheet.getSheetView();
        if (sheetView.getRowHeaderWidth() > x
                && sheetView.getColumnHeaderHeight() < y) {
            ret = true;
            sheetView.getCurrentSheet().setActiveCellType(Sheet.ACTIVECELL_ROW);
            sheetView.getCurrentSheet().setActiveCellRow(findClickedRowHeader(event));

        } else if (sheetView.getRowHeaderWidth() < x
                && sheetView.getColumnHeaderHeight() > y) {
            ret = true;
            sheetView.getCurrentSheet().setActiveCellType(Sheet.ACTIVECELL_COLUMN);
            sheetView.getCurrentSheet().setActiveCellColumn(findClickedColumnHeader(event));
        }

        spreadsheet.getControl().actionEvent(EventConstant.APP_CONTENT_SELECTED, null);

        return ret;
    }


    private void actionUp(MotionEvent event) {
        if (!actionDown) {
            return;
        }

        actionDown = false;

        boolean ret = false;

        if (scrolling) {
            ret = changeHeaderEnd();
        } else if (!longPress && checkClickedCell(event)) {
            ret = true;
        } else {
            ret = checkClickedHeader(event);
        }

        longPress = false;

        if (ret) {
            if (!timer.isRunning()) {
                timer.start();
            } else {
                timer.restart();
            }
        }

    }


    public boolean onTouch(View v, MotionEvent event) {
        if (this.spreadsheet == null) {
            return false;
        }

        super.onTouch(v, event);
        int action = event.getAction();

        if (event.getPointerCount() == 2) {
            scrolling = true;
            actionDown = false;
            if (newHeaderArea != null) {
                spreadsheet.getSheetView().setDrawMovingHeaderLine(false);
                oldHeaderArea = null;
                newHeaderArea = null;
            }
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                actionDown = true;
                break;

            case MotionEvent.ACTION_MOVE:
                changingHeader(event);
                spreadsheet.abortDrawing();
                spreadsheet.postInvalidate();
                break;

            case MotionEvent.ACTION_UP:
                actionUp(event);
                scrolling = false;
                actionDown = false;
                spreadsheet.postInvalidate();
                break;

            default:
                break;
        }
        return false;
    }


    private void findChangingRowHeader(MotionEvent event) {
        float y = event.getY();

        SheetView sheetView = spreadsheet.getSheetView();
        DrawingCell cellInfor = new DrawingCell();

        cellInfor.setTop(sheetView.getColumnHeaderHeight());
        cellInfor.setRowIndex(sheetView.getMinRowAndColumnInformation().getMinRowIndex());

        int oldTop = Math.round(cellInfor.getTop());
        Rect rect = new Rect();
        rect.top = rect.bottom = oldTop;

        int maxRows = sheetView.getCurrentSheet().getWorkbook().isBefore07Version() ? Workbook.MAXROW_03 : Workbook.MAXROW_07;
        while (cellInfor.getTop() <= y && cellInfor.getRowIndex() <= maxRows) {
            Row row = sheetView.getCurrentSheet().getRow(cellInfor.getRowIndex());
            if (row != null && row.isZeroHeight()) {
                cellInfor.setRowIndex(cellInfor.getRowIndex() + 1);
                continue;
            }

            cellInfor.setHeight(row == null ? sheetView.getCurrentSheet().getDefaultRowHeight() : row.getRowPixelHeight());
            cellInfor.setHeight(Math.round(cellInfor.getHeight() * sheetView.getZoom()));

            if (cellInfor.getRowIndex() == sheetView.getMinRowAndColumnInformation().getMinRowIndex()
                    && !sheetView.getMinRowAndColumnInformation().isRowAllVisible()) {
                cellInfor.setVisibleHeight(
                        Math.round(
                                sheetView.getMinRowAndColumnInformation().getVisibleRowHeight()
                                        * sheetView.getZoom()));
            } else {
                cellInfor.setVisibleHeight(cellInfor.getHeight());
            }

            rect.top = rect.bottom;
            rect.bottom = Math.round(cellInfor.getTop());

            oldTop = Math.round(cellInfor.getTop());
            cellInfor.setTop(cellInfor.getTop() + cellInfor.getVisibleHeight());
            cellInfor.setRowIndex(cellInfor.getRowIndex() + 1);
        }


        if (oldHeaderArea == null) {
            oldHeaderArea = new FocusCell();
        }
        oldHeaderArea.setType(FocusCell.ROWHEADER);

        int row = 0;
        if (y > (oldTop + cellInfor.getTop()) / 2) {
            oldHeaderArea.setRow(cellInfor.getRowIndex() - 1);
            rect.top = rect.bottom;
            rect.bottom = Math.round(cellInfor.getTop());
            oldHeaderArea.setRect(rect);
        } else {
            row = cellInfor.getRowIndex() - 2;
            oldHeaderArea.setRow(row >= 0 ? row : 0);
            oldHeaderArea.setRect(rect);
        }
    }


    private void findChangingColumnHeader(MotionEvent event) {
        float x = event.getX();

        SheetView sheetView = spreadsheet.getSheetView();
        DrawingCell cellInfor = new DrawingCell();

        cellInfor.setLeft(sheetView.getRowHeaderWidth());
        cellInfor.setColumnIndex(sheetView.getMinRowAndColumnInformation().getMinColumnIndex());

        int oldLeft = Math.round(cellInfor.getLeft());
        Rect rect = new Rect();
        rect.left = rect.right = Math.round(cellInfor.getLeft());

        int maxColumns = sheetView.getCurrentSheet().getWorkbook().isBefore07Version() ? Workbook.MAXCOLUMN_03 : Workbook.MAXCOLUMN_07;
        while (cellInfor.getLeft() <= x && cellInfor.getColumnIndex() <= maxColumns) {
            if (sheetView.getCurrentSheet().isColumnHidden(cellInfor.getColumnIndex())) {
                cellInfor.setColumnIndex(cellInfor.getColumnIndex() + 1);
                continue;
            }
            cellInfor.setWidth(Math.round(sheetView.getCurrentSheet().getColumnPixelWidth(cellInfor.getColumnIndex()) * sheetView.getZoom()));



            if (cellInfor.getColumnIndex() == sheetView.getMinRowAndColumnInformation().getMinColumnIndex()
                    && !sheetView.getMinRowAndColumnInformation().isColumnAllVisible()) {
                cellInfor.setVisibleWidth(Math.round(sheetView.getMinRowAndColumnInformation().getVisibleColumnWidth() * sheetView.getZoom()));
            } else {
                cellInfor.setVisibleWidth(cellInfor.getWidth());

            }

            rect.left = rect.right;
            rect.right = Math.round(cellInfor.getLeft());

            oldLeft = Math.round(cellInfor.getLeft());

            cellInfor.setLeft(cellInfor.getLeft() + cellInfor.getVisibleWidth());
            cellInfor.setColumnIndex(cellInfor.getColumnIndex() + 1);
        }


        if (oldHeaderArea == null) {
            oldHeaderArea = new FocusCell();
        }
        oldHeaderArea.setType(FocusCell.COLUMNHEADER);


        if (x > (oldLeft + cellInfor.getLeft()) / 2) {
            oldHeaderArea.setColumn(cellInfor.getColumnIndex() - 1);
            rect.left = rect.right;
            rect.right = Math.round(cellInfor.getLeft());
            oldHeaderArea.setRect(rect);
        } else {
            int col = cellInfor.getColumnIndex() - 2;
            oldHeaderArea.setColumn(col >= 0 ? col : 0);
            oldHeaderArea.setRect(rect);
        }
    }


    public void onLongPress(MotionEvent e) {
        super.onLongPress(e);

        longPress = true;

        float x = e.getX();
        float y = e.getY();

        oldY = Math.round(y);
        oldX = Math.round(x);

        SheetView sheetView = spreadsheet.getSheetView();
        if (sheetView.getRowHeaderWidth() > x
                && sheetView.getColumnHeaderHeight() < y) {
            findChangingRowHeader(e);
        } else if (sheetView.getRowHeaderWidth() < x
                && sheetView.getColumnHeaderHeight() > y) {
            findChangingColumnHeader(e);
        }

        if (oldHeaderArea != null) {
            newHeaderArea = oldHeaderArea.clone();

            spreadsheet.getSheetView().changeHeaderArea(newHeaderArea);
            spreadsheet.getSheetView().setDrawMovingHeaderLine(true);

            spreadsheet.abortDrawing();
            spreadsheet.postInvalidate();
        }

    }


    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        super.onScroll(e1, e2, distanceX, distanceY);
        SheetView sheetview = spreadsheet.getSheetView();

        boolean change = false;
        if (Math.abs(distanceX) > 2) {
            change = true;
        } else {
            distanceX = 0;
        }

        if (Math.abs(distanceY) > 2) {
            change = true;
        } else {
            distanceY = 0;
        }
        if (change) {
            isScroll = true;
            scrolling = true;
            sheetview.getRowHeader().calculateRowHeaderWidth(sheetview.getZoom());
            sheetview.scrollBy(Math.round(distanceX), Math.round(distanceY));

            spreadsheet.abortDrawing();
            spreadsheet.postInvalidate();
        }
        return true;
    }


    public void fling(int velocityX, int velocityY) {
        super.fling(velocityX, velocityY);
        float zoom = spreadsheet.getSheetView().getZoom();
        int scrollX = Math.round(spreadsheet.getSheetView().getScrollX() * zoom);
        int scrollY = Math.round(spreadsheet.getSheetView().getScrollY() * zoom);


        oldY = 0;
        oldX = 0;
        if (Math.abs(velocityY) > Math.abs(velocityX)) {
            oldY = scrollY;
            mScroller.fling(scrollX, scrollY, 0, velocityY, 0, 0, 0, spreadsheet.getSheetView().getMaxScrollY());
        }

        else {
            oldX = scrollX;
            mScroller.fling(scrollX, scrollY, velocityX, 0, 0, spreadsheet.getSheetView().getMaxScrollX(), 0, 0);
        }

        spreadsheet.abortDrawing();
        spreadsheet.postInvalidate();
    }


    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            isFling = true;
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();

            if (x == oldX && oldY == y) {
                mScroller.abortAnimation();
                spreadsheet.abortDrawing();
                spreadsheet.postInvalidate();
                return;
            }
            SheetView sheetview = spreadsheet.getSheetView();
            boolean isDraw = false;

            if (x != oldX && oldY == 0) {
                if (Math.abs(x - oldX) > 2) {
                    isDraw = true;
                } else {
                    oldX = x;
                }
            }

            if (y != oldY && oldX == 0) {
                if (Math.abs(oldY - y) > 2) {
                    isDraw = true;
                } else {
                    oldY = y;
                }
            }
            if (isDraw) {
                scrolling = true;
                sheetview.getRowHeader().calculateRowHeaderWidth(sheetview.getZoom());
                sheetview.scrollBy(Math.round(x - oldX), Math.round(y - oldY));
            }

            spreadsheet.abortDrawing();
            spreadsheet.postInvalidate();
            oldX = x;
            oldY = y;
        }
    }


    public void dispose() {
        super.dispose();
        spreadsheet = null;

        if (oldHeaderArea != null) {
            oldHeaderArea.dispose();
            oldHeaderArea = null;
        }

        if (newHeaderArea != null) {
            newHeaderArea.dispose();
            newHeaderArea = null;
        }

        if (timer != null) {
            timer.dispose();
            timer = null;
        }
    }
}
