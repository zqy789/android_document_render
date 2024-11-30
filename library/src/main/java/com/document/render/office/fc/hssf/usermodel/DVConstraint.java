

package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.ss.usermodel.DataValidationConstraint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class DVConstraint implements DataValidationConstraint {

    private static final ValidationType VT = null;
    private final int _validationType;
    private int _operator;
    private String[] _explicitListValues;
    private String _formula1;
    private String _formula2;
    private Double _value1;
    private Double _value2;
    private DVConstraint(int validationType, int comparisonOperator, String formulaA,
                         String formulaB, Double value1, Double value2, String[] excplicitListValues) {
        _validationType = validationType;
        _operator = comparisonOperator;
        _formula1 = formulaA;
        _formula2 = formulaB;
        _value1 = value1;
        _value2 = value2;
        _explicitListValues = excplicitListValues;
    }



    private DVConstraint(String listFormula, String[] excplicitListValues) {
        this(ValidationType.LIST, OperatorType.IGNORED,
                listFormula, null, null, null, excplicitListValues);
    }


    public static DVConstraint createNumericConstraint(int validationType, int comparisonOperator,
                                                       String expr1, String expr2) {
        switch (validationType) {
            case ValidationType.ANY:
                if (expr1 != null || expr2 != null) {
                    throw new IllegalArgumentException("expr1 and expr2 must be null for validation type 'any'");
                }
                break;
            case ValidationType.DECIMAL:
            case ValidationType.INTEGER:
            case ValidationType.TEXT_LENGTH:
                if (expr1 == null) {
                    throw new IllegalArgumentException("expr1 must be supplied");
                }
                OperatorType.validateSecondArg(comparisonOperator, expr2);
                break;
            default:
                throw new IllegalArgumentException("Validation Type ("
                        + validationType + ") not supported with this method");
        }

        String formula1 = getFormulaFromTextExpression(expr1);
        Double value1 = formula1 == null ? convertNumber(expr1) : null;

        String formula2 = getFormulaFromTextExpression(expr2);
        Double value2 = formula2 == null ? convertNumber(expr2) : null;
        return new DVConstraint(validationType, comparisonOperator, formula1, formula2, value1, value2, null);
    }

    public static DVConstraint createFormulaListConstraint(String listFormula) {
        return new DVConstraint(listFormula, null);
    }

    public static DVConstraint createExplicitListConstraint(String[] explicitListValues) {
        return new DVConstraint(null, explicitListValues);
    }


    public static DVConstraint createTimeConstraint(int comparisonOperator, String expr1, String expr2) {
        if (expr1 == null) {
            throw new IllegalArgumentException("expr1 must be supplied");
        }
        OperatorType.validateSecondArg(comparisonOperator, expr1);


        String formula1 = getFormulaFromTextExpression(expr1);
        Double value1 = formula1 == null ? convertTime(expr1) : null;

        String formula2 = getFormulaFromTextExpression(expr2);
        Double value2 = formula2 == null ? convertTime(expr2) : null;
        return new DVConstraint(VT.TIME, comparisonOperator, formula1, formula2, value1, value2, null);
    }


    public static DVConstraint createDateConstraint(int comparisonOperator, String expr1, String expr2, String dateFormat) {
        if (expr1 == null) {
            throw new IllegalArgumentException("expr1 must be supplied");
        }
        OperatorType.validateSecondArg(comparisonOperator, expr2);
        SimpleDateFormat df = dateFormat == null ? null : new SimpleDateFormat(dateFormat);


        String formula1 = getFormulaFromTextExpression(expr1);
        Double value1 = formula1 == null ? convertDate(expr1, df) : null;

        String formula2 = getFormulaFromTextExpression(expr2);
        Double value2 = formula2 == null ? convertDate(expr2, df) : null;
        return new DVConstraint(VT.DATE, comparisonOperator, formula1, formula2, value1, value2, null);
    }


    private static String getFormulaFromTextExpression(String textExpr) {
        if (textExpr == null) {
            return null;
        }
        if (textExpr.length() < 1) {
            throw new IllegalArgumentException("Empty string is not a valid formula/value expression");
        }
        if (textExpr.charAt(0) == '=') {
            return textExpr.substring(1);
        }
        return null;
    }


    private static Double convertNumber(String numberStr) {
        if (numberStr == null) {
            return null;
        }
        try {
            return new Double(numberStr);
        } catch (NumberFormatException e) {
            throw new RuntimeException("The supplied text '" + numberStr
                    + "' could not be parsed as a number");
        }
    }


    private static Double convertTime(String timeStr) {
        if (timeStr == null) {
            return null;
        }
        return new Double(HSSFDateUtil.convertTime(timeStr));
    }


    private static Double convertDate(String dateStr, SimpleDateFormat dateFormat) {
        if (dateStr == null) {
            return null;
        }
        Date dateVal;
        if (dateFormat == null) {
            dateVal = HSSFDateUtil.parseYYYYMMDDDate(dateStr);
        } else {
            try {
                dateVal = dateFormat.parse(dateStr);
            } catch (ParseException e) {
                throw new RuntimeException("Failed to parse date '" + dateStr
                        + "' using specified format '" + dateFormat + "'", e);
            }
        }
        return new Double(HSSFDateUtil.getExcelDate(dateVal));
    }

    public static DVConstraint createCustomFormulaConstraint(String formula) {
        if (formula == null) {
            throw new IllegalArgumentException("formula must be supplied");
        }
        return new DVConstraint(VT.FORMULA, OperatorType.IGNORED, formula, null, null, null, null);
    }


    private static Ptg[] convertDoubleFormula(String formula, Double value, HSSFSheet sheet) {











        return null;
    }


    public int getValidationType() {
        return _validationType;
    }


    public boolean isListValidationType() {
        return _validationType == VT.LIST;
    }


    public boolean isExplicitList() {
        return _validationType == VT.LIST && _explicitListValues != null;
    }


    public int getOperator() {
        return _operator;
    }


    public void setOperator(int operator) {
        _operator = operator;
    }


    public String[] getExplicitListValues() {
        return _explicitListValues;
    }


    public void setExplicitListValues(String[] explicitListValues) {
        if (_validationType != VT.LIST) {
            throw new RuntimeException("Cannot setExplicitListValues on non-list constraint");
        }
        _formula1 = null;
        _explicitListValues = explicitListValues;
    }


    public String getFormula1() {
        return _formula1;
    }


    public void setFormula1(String formula1) {
        _value1 = null;
        _explicitListValues = null;
        _formula1 = formula1;
    }


    public String getFormula2() {
        return _formula2;
    }


    public void setFormula2(String formula2) {
        _value2 = null;
        _formula2 = formula2;
    }


    public Double getValue1() {
        return _value1;
    }


    public void setValue1(double value1) {
        _formula1 = null;
        _value1 = new Double(value1);
    }


    public Double getValue2() {
        return _value2;
    }


    public void setValue2(double value2) {
        _formula2 = null;
        _value2 = new Double(value2);
    }


     FormulaPair createFormulas(HSSFSheet sheet) {
        Ptg[] formula1;
        Ptg[] formula2;
        if (isListValidationType()) {
            formula1 = createListFormula(sheet);
            formula2 = Ptg.EMPTY_PTG_ARRAY;
        } else {
            formula1 = convertDoubleFormula(_formula1, _value1, sheet);
            formula2 = convertDoubleFormula(_formula2, _value2, sheet);
        }
        return new FormulaPair(formula1, formula2);
    }

    private Ptg[] createListFormula(HSSFSheet sheet) {


















        return null;
    }

     public static final class FormulaPair {

        private final Ptg[] _formula1;
        private final Ptg[] _formula2;

        public FormulaPair(Ptg[] formula1, Ptg[] formula2) {
            _formula1 = formula1;
            _formula2 = formula2;
        }

        public Ptg[] getFormula1() {
            return _formula1;
        }

        public Ptg[] getFormula2() {
            return _formula2;
        }

    }
}
