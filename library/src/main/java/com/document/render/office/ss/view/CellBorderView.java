
package com.document.render.office.ss.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.document.render.office.common.PaintKit;
import com.document.render.office.ss.model.CellRangeAddress;
import com.document.render.office.ss.model.baseModel.Cell;
import com.document.render.office.ss.model.baseModel.Row;
import com.document.render.office.ss.model.baseModel.Sheet;
import com.document.render.office.ss.model.baseModel.Workbook;
import com.document.render.office.ss.model.style.CellStyle;
import com.document.render.office.ss.model.table.SSTableCellStyle;


public class CellBorderView {

    private SheetView sheetView;

    public CellBorderView(SheetView sheetView) {
        this.sheetView = sheetView;
    }


    public void draw(Canvas canvas, Cell cell, RectF rect, SSTableCellStyle tableCellStyle) {
        Paint paint = PaintKit.instance().getPaint();

        int oldColor = paint.getColor();

        Workbook book = sheetView.getSpreadsheet().getWorkbook();

        canvas.save();
        int colorIndex;
        int color;

        if (rect.left > sheetView.getRowHeaderWidth()) {
            if ((colorIndex = LeftBorder(cell)) > -1) {
                paint.setColor(book.getColor(colorIndex));
                canvas.drawRect(rect.left, rect.top, rect.left + 1, rect.bottom, paint);
            } else if (tableCellStyle != null && tableCellStyle.getBorderColor() != null) {
                paint.setColor(tableCellStyle.getBorderColor());
                canvas.drawRect(rect.left, rect.top, rect.left + 1, rect.bottom, paint);
            }
        }


        if (rect.top > sheetView.getColumnHeaderHeight()) {
            if ((colorIndex = TopBorder(cell)) > -1) {
                paint.setColor(book.getColor(colorIndex));
                canvas.drawRect(rect.left, rect.top, rect.right, rect.top + 1, paint);
            } else if (tableCellStyle != null && tableCellStyle.getBorderColor() != null) {
                paint.setColor(tableCellStyle.getBorderColor());
                canvas.drawRect(rect.left, rect.top, rect.right, rect.top + 1, paint);
            }
        }


        if (rect.right > sheetView.getRowHeaderWidth()) {
            if ((colorIndex = RightBorder(cell)) > -1) {
                paint.setColor(book.getColor(colorIndex));
                canvas.drawRect(rect.right, rect.top, rect.right + 1, rect.bottom, paint);
            } else if (tableCellStyle != null && tableCellStyle.getBorderColor() != null) {
                paint.setColor(tableCellStyle.getBorderColor());
                canvas.drawRect(rect.right, rect.top, rect.right + 1, rect.bottom, paint);
            }
        }


        if (rect.bottom > sheetView.getColumnHeaderHeight()) {
            if ((colorIndex = BottomBorder(cell)) > -1) {
                paint.setColor(book.getColor(colorIndex));
                canvas.drawRect(rect.left, rect.bottom, rect.right, rect.bottom + 1, paint);
            } else if (tableCellStyle != null && tableCellStyle.getBorderColor() != null) {
                paint.setColor(tableCellStyle.getBorderColor());
                canvas.drawRect(rect.left, rect.bottom, rect.right, rect.bottom + 1, paint);
            }
        }

        paint.setColor(oldColor);
        canvas.restore();
    }


    private int LeftBorder(Cell cell) {
        CellStyle style = cell.getCellStyle();
        Sheet sheet = sheetView.getCurrentSheet();
        if (cell.getRangeAddressIndex() >= 0) {
            CellRangeAddress cr = sheet.getMergeRange(cell.getRangeAddressIndex());
            Cell tempCell = sheet.getRow(cr.getFirstRow()).getCell(cr.getFirstColumn());
            if (tempCell != null) {
                style = tempCell.getCellStyle();
                cell = tempCell;
            }
        }

        boolean hasLeft = false;
        int color = -1;
        if (style != null && style.getBorderLeft() > CellStyle.BORDER_NONE) {
            hasLeft = true;
            color = style.getBorderLeftColorIdx();
        } else {
            Cell tempCell = sheet.getRowByColumnsStyle(cell.getRowNumber()).getCell(cell.getColNumber() - 1);
            if (tempCell != null) {
                style = tempCell.getCellStyle();
                if (tempCell.getRangeAddressIndex() >= 0) {
                    CellRangeAddress cr = sheet.getMergeRange(tempCell.getRangeAddressIndex());
                    tempCell = sheet.getRow(cr.getLastRow()).getCell(cr.getLastColumn());
                    if (tempCell != null) {
                        style = tempCell.getCellStyle();
                    }
                }
                if (style != null && style.getBorderRight() > CellStyle.BORDER_NONE) {
                    hasLeft = true;
                    color = style.getBorderRightColorIdx();
                }
            }
        }


        if (hasLeft && cell.getExpandedRangeAddressIndex() >= 0) {
            CellRangeAddress cr = sheet.getRow(cell.getRowNumber()).getExpandedRangeAddress(cell.getExpandedRangeAddressIndex()).getRangedAddress();
            if (cell.getColNumber() != cr.getFirstColumn()) {
                hasLeft = false;
            }
        }

        if (hasLeft) {
            return color;
        } else {
            return -1;
        }
    }


    private int RightBorder(Cell cell) {
        CellStyle style = cell.getCellStyle();
        Sheet sheet = sheetView.getCurrentSheet();
        if (cell.getRangeAddressIndex() >= 0) {
            CellRangeAddress cr = sheet.getMergeRange(cell.getRangeAddressIndex());
            Cell tempCell = sheet.getRow(cr.getLastRow()).getCell(cr.getLastColumn());
            if (tempCell != null) {
                style = tempCell.getCellStyle();
                cell = tempCell;
            }
        }
        boolean hasRight = false;
        int color = -1;
        if (style != null && style.getBorderRight() > CellStyle.BORDER_NONE) {
            hasRight = true;
            color = style.getBorderRightColorIdx();
        } else {
            Cell tempCell = sheet.getRowByColumnsStyle(cell.getRowNumber()).getCell(cell.getColNumber() + 1);
            if (tempCell != null) {

                style = tempCell.getCellStyle();
                if (tempCell.getRangeAddressIndex() >= 0) {
                    CellRangeAddress cr = sheet.getMergeRange(tempCell.getRangeAddressIndex());
                    tempCell = sheet.getRow(cr.getFirstRow()).getCell(cr.getFirstColumn());
                    if (tempCell != null) {
                        style = tempCell.getCellStyle();
                    }
                }
                if (style != null && style.getBorderLeft() > CellStyle.BORDER_NONE) {
                    hasRight = true;
                    color = style.getBorderLeftColorIdx();
                }
            }
        }

        if (hasRight && cell.getExpandedRangeAddressIndex() >= 0) {
            CellRangeAddress cr = sheet.getRow(cell.getRowNumber()).getExpandedRangeAddress(cell.getExpandedRangeAddressIndex()).getRangedAddress();
            if (cell.getColNumber() != cr.getLastColumn()) {
                hasRight = false;
            }
        }

        if (hasRight) {
            return color;
        } else {
            return -1;
        }
    }


    private int TopBorder(Cell cell) {
        CellStyle style = cell.getCellStyle();
        Sheet sheet = sheetView.getCurrentSheet();
        if (cell.getRangeAddressIndex() >= 0) {
            CellRangeAddress cr = sheet.getMergeRange(cell.getRangeAddressIndex());
            Cell tempCell = sheet.getRow(cr.getFirstRow()).getCell(cr.getFirstColumn());
            if (tempCell != null) {
                style = tempCell.getCellStyle();
                cell = tempCell;
            }
        }

        if (style != null && style.getBorderTop() > CellStyle.BORDER_NONE) {
            return style.getBorderTopColorIdx();
        }

        Row topRow = sheet.getRowByColumnsStyle(cell.getRowNumber() - 1);
        if (topRow != null) {
            cell = topRow.getCell(cell.getColNumber());
            if (cell != null) {

                style = cell.getCellStyle();
                if (cell.getRangeAddressIndex() >= 0) {
                    CellRangeAddress cr = sheet.getMergeRange(cell.getRangeAddressIndex());
                    cell = sheet.getRow(cr.getLastRow()).getCell(cr.getLastColumn());
                    if (cell != null) {
                        style = cell.getCellStyle();
                    }
                }
                if (style != null && style.getBorderBottom() > CellStyle.BORDER_NONE) {
                    return style.getBorderBottomColorIdx();
                }
            }
        }

        return -1;
    }


    private int BottomBorder(Cell cell) {
        CellStyle style = cell.getCellStyle();
        Sheet sheet = sheetView.getCurrentSheet();
        if (cell.getRangeAddressIndex() >= 0) {
            CellRangeAddress cr = sheet.getMergeRange(cell.getRangeAddressIndex());
            Cell tempCell = sheet.getRow(cr.getLastRow()).getCell(cr.getLastColumn());
            if (tempCell != null) {
                style = tempCell.getCellStyle();
                cell = tempCell;
            }
        }

        if (style != null && style.getBorderBottom() > CellStyle.BORDER_NONE) {
            return style.getBorderBottomColorIdx();
        }

        Row topRow = sheet.getRowByColumnsStyle(cell.getRowNumber() + 1);
        if (topRow != null) {
            cell = topRow.getCell(cell.getColNumber());
            if (cell != null) {

                style = cell.getCellStyle();
                if (cell.getRangeAddressIndex() >= 0) {
                    CellRangeAddress cr = sheet.getMergeRange(cell.getRangeAddressIndex());
                    cell = sheet.getRow(cr.getFirstRow()).getCell(cr.getFirstColumn());
                    if (cell != null) {
                        style = cell.getCellStyle();
                    }
                }
                if (style != null && style.getBorderTop() > CellStyle.BORDER_NONE) {
                    return style.getBorderTopColorIdx();
                }
            }
        }

        return -1;
    }


    public void drawActiveCellBorder(Canvas canvas, RectF rect, short activeCellType) {
        Rect clipBounds = canvas.getClipBounds();
        clipBounds.left = sheetView.getRowHeaderWidth();
        clipBounds.top = sheetView.getColumnHeaderHeight();

        canvas.save();
        canvas.clipRect(clipBounds);

        Paint paint = PaintKit.instance().getPaint();

        int oldColor = paint.getColor();

        paint.setColor(Color.BLACK);
        if (activeCellType == Sheet.ACTIVECELL_SINGLE && rect.left != rect.right && rect.top != rect.bottom) {


            {
                canvas.drawRect(rect.left - 2, rect.top - 2, rect.left + 1, rect.bottom + 2, paint);
            }


            {
                canvas.drawRect(rect.left - 2, rect.top - 2, rect.right + 2, rect.top + 1, paint);
            }


            {
                canvas.drawRect(rect.right - 1, rect.top - 2, rect.right + 2, rect.bottom + 2, paint);
            }


            {
                canvas.drawRect(rect.left - 2, rect.bottom - 1, rect.right + 2, rect.bottom + 2, paint);
            }
        } else if (activeCellType == Sheet.ACTIVECELL_ROW && rect.top != rect.bottom) {

            canvas.drawRect(clipBounds.left - 2, rect.top - 2, clipBounds.right + 10, rect.top + 1, paint);


            canvas.drawRect(clipBounds.left - 2, rect.bottom - 1, clipBounds.right + 10, rect.bottom + 2, paint);
        } else if (activeCellType == Sheet.ACTIVECELL_COLUMN && rect.left != rect.right) {

            canvas.drawRect(rect.left - 2, clipBounds.top - 2, rect.left + 1, clipBounds.bottom + 2, paint);


            canvas.drawRect(rect.right - 1, clipBounds.top - 2, rect.right + 2, clipBounds.bottom + 2, paint);
        }

        paint.setColor(oldColor);
        canvas.restore();
    }

    public void dispose() {
        sheetView = null;
    }
}
