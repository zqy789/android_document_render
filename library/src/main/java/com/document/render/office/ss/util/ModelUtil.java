
package com.document.render.office.ss.util;

import android.graphics.Rect;
import android.graphics.RectF;

import com.document.render.office.constant.SSConstant;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.ss.model.CellRangeAddress;
import com.document.render.office.ss.model.baseModel.Cell;
import com.document.render.office.ss.model.baseModel.Row;
import com.document.render.office.ss.model.baseModel.Sheet;
import com.document.render.office.ss.model.baseModel.Workbook;
import com.document.render.office.ss.model.drawing.CellAnchor;
import com.document.render.office.ss.model.style.CellStyle;
import com.document.render.office.ss.other.SheetScroller;
import com.document.render.office.ss.util.format.NumericFormatter;
import com.document.render.office.ss.view.SheetView;



public class ModelUtil {

    private static ModelUtil mu = new ModelUtil();

    private RectF area = new RectF();


    public static ModelUtil instance() {
        return mu;
    }

    public static Rectangle processRect(Rectangle rect, float angle) {
        angle = angle % 360;
        if ((angle > 45 && angle <= 135) || (angle > 225 && angle < 315)) {
            double centerX = rect.getCenterX();
            double centerY = rect.getCenterY();

            rect.x = (int) Math.round(centerX - rect.height / 2);
            rect.y = (int) Math.round(centerY - rect.width / 2);

            int temp = rect.width;
            rect.width = rect.height;
            rect.height = temp;
        }
        return rect;
    }


    public int getCellRangeAddressIndex(Sheet sheet, int row, int column) {
        int len = sheet.getMergeRangeCount();
        for (int i = 0; i < len; i++) {
            CellRangeAddress cra = sheet.getMergeRange(i);
            if (containsCell(cra, row, column)) {
                return i;
            }
        }
        return -1;
    }


    public CellRangeAddress getCellRangeAddress(Sheet sheet, int row, int column) {
        int len = sheet.getMergeRangeCount();
        for (int i = 0; i < len; i++) {
            CellRangeAddress cra = sheet.getMergeRange(i);
            if (containsCell(cra, row, column)) {
                return cra;
            }
        }
        return null;
    }




    public boolean containsCell(CellRangeAddress cr, int row, int col) {
        return row >= cr.getFirstRow() && row <= cr.getLastRow()
                && col >= cr.getFirstColumn() && col <= cr.getLastColumn();
    }



    public RectF getCellRangeAddressAnchor(SheetView sheetview, CellRangeAddress cellRangeAddress) {


        area.left = getValueX(sheetview, cellRangeAddress.getFirstColumn(), 0);
        area.top = getValueY(sheetview, cellRangeAddress.getFirstRow(), 0);

        area.right =
                getValueX(sheetview, cellRangeAddress.getLastColumn() + 1, 0);
        area.bottom =
                getValueY(sheetview, cellRangeAddress.getLastRow() + 1, 0);

        return area;
    }


    private Rect getCellRangeAddressAnchor(Sheet sheet, CellRangeAddress cellRangeAddress) {
        Rect area = new Rect();

        area.left = Math.round(getValueX(sheet, cellRangeAddress.getFirstColumn(), 0));
        area.top = Math.round(getValueY(sheet, cellRangeAddress.getFirstRow(), 0));

        area.right =
                Math.round(getValueX(sheet, cellRangeAddress.getLastColumn() + 1, 0));
        area.bottom =
                Math.round(getValueY(sheet, cellRangeAddress.getLastRow() + 1, 0));

        return area;
    }


    public Rectangle getCellAnchor(Sheet sheet, CellAnchor cellAnchor) {
        Rectangle area = new Rectangle();
        if (cellAnchor == null) {
            return null;
        }

        area.x = Math.round(getValueX(sheet, cellAnchor.getStart().getColumn(), cellAnchor.getStart().getDX()));
        area.y = Math.round(getValueY(sheet, cellAnchor.getStart().getRow(), cellAnchor.getStart().getDY()));

        if (cellAnchor.getType() == CellAnchor.TWOCELLANCHOR) {
            area.width = Math.round(getValueX(sheet, cellAnchor.getEnd().getColumn(), cellAnchor.getEnd().getDX()) - area.x);
            area.height = Math.round(getValueY(sheet, cellAnchor.getEnd().getRow(), cellAnchor.getEnd().getDY()) - area.y);
        } else if (cellAnchor.getType() == CellAnchor.ONECELLANCHOR) {
            area.width = cellAnchor.getWidth();
            area.height = cellAnchor.getHeight();
        }

        return area;
    }


    public RectF getCellAnchor(SheetView sheetview, Cell cell) {
        if (cell == null) {
            return null;
        }


        if (cell.getRangeAddressIndex() >= 0) {
            return getCellRangeAddressAnchor(sheetview, sheetview.getCurrentSheet().getMergeRange(cell.getRangeAddressIndex()));
        } else {


            area.left = getValueX(sheetview, cell.getColNumber(), 0);
            area.top = getValueY(sheetview, cell.getRowNumber(), 0);

            area.right = getValueX(sheetview, cell.getColNumber() + 1, 0);
            area.bottom = getValueY(sheetview, cell.getRowNumber() + 1, 0);


            return area;
        }

    }


    public RectF getCellAnchor(SheetView sheetview, int row, int column) {
        Sheet sheet = sheetview.getCurrentSheet();

        if (sheet.getRow(row) != null && sheet.getRow(row).getCell(column) != null) {
            Cell cell = sheet.getRow(row).getCell(column);
            if (cell.getRangeAddressIndex() >= 0) {
                return getCellRangeAddressAnchor(sheetview, sheet.getMergeRange(cell.getRangeAddressIndex()));
            }
        }


        area.left = getValueX(sheetview, column, 0);
        area.top = getValueY(sheetview, row, 0);

        area.right = getValueX(sheetview, column + 1, 0);
        area.bottom = getValueY(sheetview, row + 1, 0);


        return area;
    }


    public RectF getCellAnchor(SheetView sheetview, int row, int column1, int column2) {
        Sheet sheet = sheetview.getCurrentSheet();

        if (sheet.getRow(row) != null && sheet.getRow(row).getCell(column1) != null && sheet.getRow(row).getCell(column2) != null) {
            Cell cell = sheet.getRow(row).getCell(column2);
            if (cell.getRangeAddressIndex() >= 0) {
                column2 = sheet.getMergeRange(cell.getRangeAddressIndex()).getLastColumn();
            }
        }


        area.left = getValueX(sheetview, column1, 0);
        area.top = getValueY(sheetview, row, 0);

        area.right = getValueX(sheetview, column2 + 1, 0);
        area.bottom = getValueY(sheetview, row + 1, 0);

        return area;
    }


    public Rect getCellAnchor(Sheet sheet, int row, int column) {
        if (sheet.getRow(row) != null && sheet.getRow(row).getCell(column) != null) {
            Cell cell = sheet.getRow(row).getCell(column);
            if (cell.getRangeAddressIndex() >= 0) {
                return getCellRangeAddressAnchor(sheet, sheet.getMergeRange(cell.getRangeAddressIndex()));
            }
        }

        Rect area = new Rect();

        area.left = Math.round(getValueX(sheet, column, 0));
        area.top = Math.round(getValueY(sheet, row, 0));

        area.right = Math.round(getValueX(sheet, column + 1, 0));
        area.bottom = Math.round(getValueY(sheet, row + 1, 0));

        return area;
    }


    public Rect getCellAnchor(Sheet sheet, int row, int column, boolean ignoreMergerCell) {
        if (!ignoreMergerCell && sheet.getRow(row) != null && sheet.getRow(row).getCell(column) != null) {
            Cell cell = sheet.getRow(row).getCell(column);
            if (cell.getRangeAddressIndex() >= 0) {
                return getCellRangeAddressAnchor(sheet, sheet.getMergeRange(cell.getRangeAddressIndex()));
            }

            return null;
        } else {
            Rect area = new Rect();

            area.left = Math.round(getValueX(sheet, column, 0));
            area.top = Math.round(getValueY(sheet, row, 0));

            area.right = Math.round(getValueX(sheet, column + 1, 0));
            area.bottom = Math.round(getValueY(sheet, row + 1, 0));

            return area;
        }
    }


    private float getValueX(Sheet sheet, int columnIndex, int dx) {
        float x = 0;

        for (int i = 0; i < columnIndex; i++) {
            if (sheet.isColumnHidden(i)) {
                continue;
            }
            x += sheet.getColumnPixelWidth(i);
        }

        return dx + x;
    }

    private float getValueY(Sheet sheet, int rowIndex, int dy) {
        float y = 0;
        float h = 0;
        for (int i = 0; i < rowIndex; i++) {
            Row row = sheet.getRow(i);
            if (row != null && row.isZeroHeight()) {
                continue;
            }
            h = row == null ? sheet.getDefaultRowHeight() : row.getRowPixelHeight();
            y += h;
        }

        return y + dy;
    }

    public float getValueX(SheetView sheetview, int columnIndex, float dx) {
        float x = sheetview.getRowHeaderWidth();
        float w = 0;
        Sheet sheet = sheetview.getCurrentSheet();
        SheetScroller minRowAndColumnInformation = sheetview.getMinRowAndColumnInformation();
        int colStart = minRowAndColumnInformation.getMinColumnIndex() > 0 ? minRowAndColumnInformation.getMinColumnIndex() : 0;
        if (colStart < columnIndex && !minRowAndColumnInformation.isColumnAllVisible()) {
            colStart += 1;
            x += (minRowAndColumnInformation.getVisibleColumnWidth() * sheetview.getZoom());
        }

        int maxColumns = sheet.getWorkbook().isBefore07Version() ? Workbook.MAXCOLUMN_03 : Workbook.MAXCOLUMN_07;
        while (colStart < columnIndex && colStart <= maxColumns) {
            if (sheet.isColumnHidden(colStart)) {
                colStart++;
                continue;
            }

            w = (sheet.getColumnPixelWidth(colStart) * sheetview.getZoom());

            x += w;
            colStart++;
        }

        return dx + x;
    }


    public float getValueY(SheetView sheetview, int rowIndex, float dy) {
        float y = (SSConstant.DEFAULT_COLUMN_HEADER_HEIGHT * sheetview.getZoom());
        float h = 0;
        Sheet sheet = sheetview.getCurrentSheet();
        Row row;
        SheetScroller minRowAndColumnInformation = sheetview.getMinRowAndColumnInformation();
        int rowStart = minRowAndColumnInformation.getMinRowIndex() > 0 ? minRowAndColumnInformation.getMinRowIndex() : 0;
        if (rowStart < rowIndex && !minRowAndColumnInformation.isRowAllVisible()) {
            rowStart += 1;
            y += (minRowAndColumnInformation.getVisibleRowHeight() * sheetview.getZoom());
        }

        int maxRows = sheet.getWorkbook().isBefore07Version() ? Workbook.MAXROW_03 : Workbook.MAXROW_07;
        while (rowStart < rowIndex && rowStart <= maxRows) {
            row = sheet.getRow(rowStart);

            if (row != null && row.isZeroHeight()) {
                rowStart++;
                continue;
            }

            h = row == null ? sheetview.getCurrentSheet().getDefaultRowHeight() : row.getRowPixelHeight();
            h = (h * sheetview.getZoom());

            y += h;
            rowStart++;
        }

        return y + dy;
    }


    public String getFormatContents(Workbook book, Cell cell) {

        if (!cell.hasValidValue()) {
            return null;
        }

        CellStyle style = cell.getCellStyle();
        String value = "";

        short numericType;

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                value = String.valueOf(cell.getBooleanValue()).toUpperCase();
                break;

            case Cell.CELL_TYPE_NUMERIC:
                String key = style.getFormatCode();
                if (key == null) {
                    key = "General";
                    numericType = Cell.CELL_TYPE_NUMERIC_GENERAL;
                } else {
                    if (cell.getCellNumericType() > 0) {
                        numericType = cell.getCellNumericType();
                    } else {
                        numericType = NumericFormatter.instance().getNumericCellType(key);
                        cell.setCellNumericType(numericType);
                    }
                }

                try {
                    if (numericType == Cell.CELL_TYPE_NUMERIC_SIMPLEDATE) {
                        value = NumericFormatter.instance().getFormatContents(key, cell.getDateCellValue(book.isUsing1904DateWindowing()));


                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        cell.setCellValue(book.addSharedString(value));
                    } else {

                        value = NumericFormatter.instance().getFormatContents(key, cell.getNumberValue(), numericType);
                    }
                } catch (Exception ex) {
                    value = String.valueOf(cell.getNumberValue());
                }
                break;

            case Cell.CELL_TYPE_STRING:
                if (cell.getStringCellValueIndex() >= 0) {
                    value = book.getSharedString(cell.getStringCellValueIndex());
                }
                break;
            case Cell.CELL_TYPE_FORMULA:
                break;

            case Cell.CELL_TYPE_ERROR:
                value = ErrorEval.getText(cell.getErrorValue());
                break;
            default:
                break;
        }
        return value;
    }
}
