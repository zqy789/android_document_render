
package com.document.render.office.fc.ss.util;

import com.document.render.office.fc.ss.usermodel.ICell;
import com.document.render.office.fc.ss.usermodel.Sheet;
import com.document.render.office.fc.ss.usermodel.Workbook;

import java.util.Calendar;
import java.util.Date;



public class SheetBuilder {

    private Workbook workbook;
    private Object[][] cells;
    private boolean shouldCreateEmptyCells = false;

    public SheetBuilder(Workbook workbook, Object[][] cells) {
        this.workbook = workbook;
        this.cells = cells;
    }


    public boolean getCreateEmptyCells() {
        return shouldCreateEmptyCells;
    }


    public SheetBuilder setCreateEmptyCells(boolean shouldCreateEmptyCells) {
        this.shouldCreateEmptyCells = shouldCreateEmptyCells;
        return this;
    }


    public Sheet build() {

















        return null;
    }


    public void setCellValue(ICell cell, Object value) {
        if (value == null || cell == null) {
            return;
        } else if (value instanceof Number) {
            double doubleValue = ((Number) value).doubleValue();
            cell.setCellValue(doubleValue);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Calendar) {
            cell.setCellValue((Calendar) value);
        } else if (isFormulaDefinition(value)) {
            cell.setCellFormula(getFormula(value));
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private boolean isFormulaDefinition(Object obj) {
        if (obj instanceof String) {
            String str = (String) obj;
            if (str.length() < 2) {
                return false;
            } else {
                return ((String) obj).charAt(0) == '=';
            }
        } else {
            return false;
        }
    }

    private String getFormula(Object obj) {
        return ((String) obj).substring(1);
    }
}
