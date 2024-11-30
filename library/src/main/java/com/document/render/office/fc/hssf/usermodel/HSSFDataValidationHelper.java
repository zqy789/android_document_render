

package com.document.render.office.fc.hssf.usermodel;


import com.document.render.office.fc.ss.usermodel.DataValidation;
import com.document.render.office.fc.ss.usermodel.DataValidationConstraint;
import com.document.render.office.fc.ss.usermodel.DataValidationConstraint.ValidationType;
import com.document.render.office.fc.ss.usermodel.DataValidationHelper;
import com.document.render.office.fc.ss.util.CellRangeAddressList;



public class HSSFDataValidationHelper implements DataValidationHelper {
    @SuppressWarnings("unused")
    private HSSFSheet sheet;

    public HSSFDataValidationHelper(HSSFSheet sheet) {
        super();
        this.sheet = sheet;
    }


    public DataValidationConstraint createDateConstraint(int operatorType, String formula1, String formula2, String dateFormat) {
        return DVConstraint.createDateConstraint(operatorType, formula1, formula2, dateFormat);
    }


    public DataValidationConstraint createExplicitListConstraint(String[] listOfValues) {
        return DVConstraint.createExplicitListConstraint(listOfValues);
    }


    public DataValidationConstraint createFormulaListConstraint(String listFormula) {
        return DVConstraint.createFormulaListConstraint(listFormula);
    }


    public DataValidationConstraint createNumericConstraint(int validationType, int operatorType, String formula1, String formula2) {
        return DVConstraint.createNumericConstraint(validationType, operatorType, formula1, formula2);
    }

    public DataValidationConstraint createIntegerConstraint(int operatorType, String formula1, String formula2) {
        return DVConstraint.createNumericConstraint(ValidationType.INTEGER, operatorType, formula1, formula2);
    }


    public DataValidationConstraint createDecimalConstraint(int operatorType, String formula1, String formula2) {
        return DVConstraint.createNumericConstraint(ValidationType.DECIMAL, operatorType, formula1, formula2);
    }


    public DataValidationConstraint createTextLengthConstraint(int operatorType, String formula1, String formula2) {
        return DVConstraint.createNumericConstraint(ValidationType.TEXT_LENGTH, operatorType, formula1, formula2);
    }


    public DataValidationConstraint createTimeConstraint(int operatorType, String formula1, String formula2) {
        return DVConstraint.createTimeConstraint(operatorType, formula1, formula2);
    }


    public DataValidationConstraint createCustomConstraint(String formula) {
        return DVConstraint.createCustomFormulaConstraint(formula);
    }


    public DataValidation createValidation(DataValidationConstraint constraint, CellRangeAddressList cellRangeAddressList) {
        return new HSSFDataValidation(cellRangeAddressList, constraint);
    }
}
