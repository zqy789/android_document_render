

package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.fc.hssf.formula.EvaluationCell;
import com.document.render.office.fc.hssf.formula.EvaluationSheet;
import com.document.render.office.ss.model.XLSModel.ACell;
import com.document.render.office.ss.model.XLSModel.ASheet;


final class HSSFEvaluationCell implements EvaluationCell {

    private  EvaluationSheet _evalSheet;
    private  ACell _cell;

    public HSSFEvaluationCell(ACell cell, EvaluationSheet evalSheet) {
        _cell = cell;
        _evalSheet = evalSheet;
    }

    public HSSFEvaluationCell(ACell cell) {
        this(cell, new HSSFEvaluationSheet((ASheet) cell.getSheet()));
    }

    public Object getIdentityKey() {


        return _cell;
    }

    public void setHSSFCell(ACell cell) {
        _cell = cell;
        if (_evalSheet != null) {
            ((HSSFEvaluationSheet) _evalSheet).setASheet((ASheet) cell.getSheet());
        } else {
            _evalSheet = new HSSFEvaluationSheet((ASheet) cell.getSheet());
        }
    }

    public ACell getACell() {
        return _cell;
    }

    public boolean getBooleanCellValue() {
        return _cell.getBooleanCellValue();
    }

    public int getCellType() {
        return _cell.getCellType();
    }

    public int getColumnIndex() {
        return _cell.getColNumber();
    }

    public int getErrorCellValue() {
        return _cell.getErrorCellValue();
    }

    public double getNumericCellValue() {
        return _cell.getNumericCellValue();
    }

    public int getRowIndex() {
        return _cell.getRowNumber();
    }

    public EvaluationSheet getSheet() {
        return _evalSheet;
    }

    public String getStringCellValue() {


        return _cell.getStringCellValue();
    }
}
