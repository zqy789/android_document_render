

package com.document.render.office.fc.hssf.usermodel;


import com.document.render.office.fc.hssf.record.DVRecord;
import com.document.render.office.fc.hssf.usermodel.DVConstraint.FormulaPair;
import com.document.render.office.fc.ss.usermodel.DataValidation;
import com.document.render.office.fc.ss.usermodel.DataValidationConstraint;
import com.document.render.office.fc.ss.usermodel.DataValidationConstraint.ValidationType;
import com.document.render.office.fc.ss.util.CellRangeAddressList;



public final class HSSFDataValidation implements DataValidation {
    private String _prompt_title;
    private String _prompt_text;
    private String _error_title;
    private String _error_text;

    private int _errorStyle = ErrorStyle.STOP;
    private boolean _emptyCellAllowed = true;
    private boolean _suppress_dropdown_arrow = false;
    private boolean _showPromptBox = true;
    private boolean _showErrorBox = true;
    private CellRangeAddressList _regions;
    private DVConstraint _constraint;


    public HSSFDataValidation(CellRangeAddressList regions, DataValidationConstraint constraint) {
        _regions = regions;


        _constraint = (DVConstraint) constraint;
    }



    public DataValidationConstraint getValidationConstraint() {
        return _constraint;
    }

    public DVConstraint getConstraint() {
        return _constraint;
    }

    public CellRangeAddressList getRegions() {
        return _regions;
    }


    public int getErrorStyle() {
        return _errorStyle;
    }


    public void setErrorStyle(int error_style) {
        _errorStyle = error_style;
    }


    public boolean getEmptyCellAllowed() {
        return _emptyCellAllowed;
    }


    public void setEmptyCellAllowed(boolean allowed) {
        _emptyCellAllowed = allowed;
    }


    public boolean getSuppressDropDownArrow() {
        if (_constraint.getValidationType() == ValidationType.LIST) {
            return _suppress_dropdown_arrow;
        }
        return false;
    }


    public void setSuppressDropDownArrow(boolean suppress) {
        _suppress_dropdown_arrow = suppress;
    }


    public boolean getShowPromptBox() {
        return _showPromptBox;
    }


    public void setShowPromptBox(boolean show) {
        _showPromptBox = show;
    }


    public boolean getShowErrorBox() {
        return _showErrorBox;
    }


    public void setShowErrorBox(boolean show) {
        _showErrorBox = show;
    }


    public void createPromptBox(String title, String text) {
        _prompt_title = title;
        _prompt_text = text;
        this.setShowPromptBox(true);
    }


    public String getPromptBoxTitle() {
        return _prompt_title;
    }


    public String getPromptBoxText() {
        return _prompt_text;
    }


    public void createErrorBox(String title, String text) {
        _error_title = title;
        _error_text = text;
        this.setShowErrorBox(true);
    }


    public String getErrorBoxTitle() {
        return _error_title;
    }


    public String getErrorBoxText() {
        return _error_text;
    }

    public DVRecord createDVRecord(HSSFSheet sheet) {

        FormulaPair fp = _constraint.createFormulas(sheet);

        return new DVRecord(_constraint.getValidationType(),
                _constraint.getOperator(),
                _errorStyle, _emptyCellAllowed, getSuppressDropDownArrow(),
                _constraint.getValidationType() == ValidationType.LIST && _constraint.getExplicitListValues() != null,
                _showPromptBox, _prompt_title, _prompt_text,
                _showErrorBox, _error_title, _error_text,
                fp.getFormula1(), fp.getFormula2(),
                _regions);
    }
}
