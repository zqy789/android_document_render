

package com.document.render.office.fc.ss.util;

import com.document.render.office.fc.ss.usermodel.ICell;
import com.document.render.office.fc.ss.usermodel.IRow;
import com.document.render.office.fc.ss.usermodel.Sheet;
import com.document.render.office.fc.ss.usermodel.Workbook;


public final class RegionUtil {

    private RegionUtil() {

    }


    public static void setBorderLeft(int border, HSSFCellRangeAddress region, Sheet sheet,
                                     Workbook workbook) {
        int rowStart = region.getFirstRow();
        int rowEnd = region.getLastRow();
        int column = region.getFirstColumn();

        CellPropertySetter cps = new CellPropertySetter(workbook, CellUtil.BORDER_LEFT, border);
        for (int i = rowStart; i <= rowEnd; i++) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }


    public static void setLeftBorderColor(int color, HSSFCellRangeAddress region, Sheet sheet,
                                          Workbook workbook) {
        int rowStart = region.getFirstRow();
        int rowEnd = region.getLastRow();
        int column = region.getFirstColumn();

        CellPropertySetter cps = new CellPropertySetter(workbook, CellUtil.LEFT_BORDER_COLOR,
                color);
        for (int i = rowStart; i <= rowEnd; i++) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }


    public static void setBorderRight(int border, HSSFCellRangeAddress region, Sheet sheet,
                                      Workbook workbook) {
        int rowStart = region.getFirstRow();
        int rowEnd = region.getLastRow();
        int column = region.getLastColumn();

        CellPropertySetter cps = new CellPropertySetter(workbook, CellUtil.BORDER_RIGHT, border);
        for (int i = rowStart; i <= rowEnd; i++) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }


    public static void setRightBorderColor(int color, HSSFCellRangeAddress region, Sheet sheet,
                                           Workbook workbook) {
        int rowStart = region.getFirstRow();
        int rowEnd = region.getLastRow();
        int column = region.getLastColumn();

        CellPropertySetter cps = new CellPropertySetter(workbook, CellUtil.RIGHT_BORDER_COLOR,
                color);
        for (int i = rowStart; i <= rowEnd; i++) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }


    public static void setBorderBottom(int border, HSSFCellRangeAddress region, Sheet sheet,
                                       Workbook workbook) {
        int colStart = region.getFirstColumn();
        int colEnd = region.getLastColumn();
        int rowIndex = region.getLastRow();
        CellPropertySetter cps = new CellPropertySetter(workbook, CellUtil.BORDER_BOTTOM, border);
        IRow row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; i++) {
            cps.setProperty(row, i);
        }
    }


    public static void setBottomBorderColor(int color, HSSFCellRangeAddress region, Sheet sheet,
                                            Workbook workbook) {
        int colStart = region.getFirstColumn();
        int colEnd = region.getLastColumn();
        int rowIndex = region.getLastRow();
        CellPropertySetter cps = new CellPropertySetter(workbook, CellUtil.BOTTOM_BORDER_COLOR,
                color);
        IRow row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; i++) {
            cps.setProperty(row, i);
        }
    }


    public static void setBorderTop(int border, HSSFCellRangeAddress region, Sheet sheet,
                                    Workbook workbook) {
        int colStart = region.getFirstColumn();
        int colEnd = region.getLastColumn();
        int rowIndex = region.getFirstRow();
        CellPropertySetter cps = new CellPropertySetter(workbook, CellUtil.BORDER_TOP, border);
        IRow row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; i++) {
            cps.setProperty(row, i);
        }
    }


    public static void setTopBorderColor(int color, HSSFCellRangeAddress region, Sheet sheet,
                                         Workbook workbook) {
        int colStart = region.getFirstColumn();
        int colEnd = region.getLastColumn();
        int rowIndex = region.getFirstRow();
        CellPropertySetter cps = new CellPropertySetter(workbook, CellUtil.TOP_BORDER_COLOR, color);
        IRow row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; i++) {
            cps.setProperty(row, i);
        }
    }


    private static final class CellPropertySetter {

        private final Workbook _workbook;
        private final String _propertyName;
        private final Short _propertyValue;


        public CellPropertySetter(Workbook workbook, String propertyName, int value) {
            _workbook = workbook;
            _propertyName = propertyName;
            _propertyValue = Short.valueOf((short) value);
        }


        public void setProperty(IRow row, int column) {
            ICell cell = CellUtil.getCell(row, column);
            CellUtil.setCellStyleProperty(cell, _workbook, _propertyName, _propertyValue);
        }
    }
}
