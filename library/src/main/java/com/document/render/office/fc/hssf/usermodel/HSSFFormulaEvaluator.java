

package com.document.render.office.fc.hssf.usermodel;

import androidx.annotation.Keep;

import com.document.render.office.fc.hssf.formula.CollaboratingWorkbooksEnvironment;
import com.document.render.office.fc.hssf.formula.IStabilityClassifier;
import com.document.render.office.fc.hssf.formula.WorkbookEvaluator;
import com.document.render.office.fc.hssf.formula.eval.BoolEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.StringEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.fc.hssf.formula.udf.UDFFinder;
import com.document.render.office.fc.ss.usermodel.CellValue;
import com.document.render.office.fc.ss.usermodel.FormulaEvaluator;
import com.document.render.office.fc.ss.usermodel.ICell;
import com.document.render.office.fc.ss.usermodel.Workbook;
import com.document.render.office.ss.model.XLSModel.ACell;
import com.document.render.office.ss.model.XLSModel.ASheet;
import com.document.render.office.ss.model.XLSModel.AWorkbook;



public class HSSFFormulaEvaluator implements FormulaEvaluator {

    private WorkbookEvaluator _bookEvaluator;
    private AWorkbook _book;
    private HSSFEvaluationCell hssfEvaluationCell = null;


    public HSSFFormulaEvaluator(ASheet sheet, AWorkbook workbook) {
        this(workbook);
        if (false) {
            sheet.toString();
        }
        this._book = workbook;
    }

    public HSSFFormulaEvaluator(AWorkbook workbook) {
        this(workbook, null);
        this._book = workbook;
    }


    public HSSFFormulaEvaluator(AWorkbook workbook, IStabilityClassifier stabilityClassifier) {
        this(workbook, stabilityClassifier, null);
    }


    private HSSFFormulaEvaluator(AWorkbook workbook, IStabilityClassifier stabilityClassifier,
                                 UDFFinder udfFinder) {
        _bookEvaluator = new WorkbookEvaluator(HSSFEvaluationWorkbook.create(workbook),
                stabilityClassifier, udfFinder);
    }


    @Keep
    public static HSSFFormulaEvaluator create(AWorkbook workbook,
                                              IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
        return new HSSFFormulaEvaluator(workbook, stabilityClassifier, udfFinder);
    }


    public static void setupEnvironment(String[] workbookNames, HSSFFormulaEvaluator[] evaluators) {
        WorkbookEvaluator[] wbEvals = new WorkbookEvaluator[evaluators.length];
        for (int i = 0; i < wbEvals.length; i++) {
            wbEvals[i] = evaluators[i]._bookEvaluator;
        }
        CollaboratingWorkbooksEnvironment.setup(workbookNames, wbEvals);
    }

    private static void setCellType(ICell cell, CellValue cv) {
        int cellType = cv.getCellType();
        switch (cellType) {
            case HSSFCell.CELL_TYPE_BOOLEAN:
            case HSSFCell.CELL_TYPE_ERROR:
            case HSSFCell.CELL_TYPE_NUMERIC:
            case HSSFCell.CELL_TYPE_STRING:
                cell.setCellType(cellType);
                return;
            case HSSFCell.CELL_TYPE_BLANK:

            case HSSFCell.CELL_TYPE_FORMULA:

        }
        throw new IllegalStateException("Unexpected cell value type (" + cellType + ")");
    }

    private static void setCellValue(ICell cell, CellValue cv) {
        int cellType = cv.getCellType();
        switch (cellType) {
            case HSSFCell.CELL_TYPE_BOOLEAN:
                cell.setCellValue(cv.getBooleanValue());
                break;
            case HSSFCell.CELL_TYPE_ERROR:
                cell.setCellErrorValue(cv.getErrorValue());
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                cell.setCellValue(cv.getNumberValue());
                break;
            case HSSFCell.CELL_TYPE_STRING:
                cell.setCellValue(new HSSFRichTextString(cv.getStringValue()));
                break;
            case HSSFCell.CELL_TYPE_BLANK:

            case HSSFCell.CELL_TYPE_FORMULA:

            default:
                throw new IllegalStateException("Unexpected cell value type (" + cellType + ")");
        }
    }


    public static void evaluateAllFormulaCells(AWorkbook wb) {
        evaluateAllFormulaCells(wb, new HSSFFormulaEvaluator(wb));
    }


    public static void evaluateAllFormulaCells(Workbook wb) {


    }

    private static void evaluateAllFormulaCells(Workbook wb, FormulaEvaluator evaluator) {















    }


    public void setCurrentRow(HSSFRow row) {

        if (false) {
            row.getClass();
        }
    }


    public void clearAllCachedResultValues() {
        _bookEvaluator.clearAllCachedResultValues();
    }


    public void notifyUpdateCell(ACell cell) {
        _bookEvaluator.notifyUpdateCell(new HSSFEvaluationCell(cell));
    }

    public void notifyUpdateCell(ICell cell) {
        _bookEvaluator.notifyUpdateCell(new HSSFEvaluationCell((ACell) cell));
    }


    public void notifyDeleteCell(ACell cell) {
        _bookEvaluator.notifyDeleteCell(new HSSFEvaluationCell(cell));
    }

    public void notifyDeleteCell(ICell cell) {
        _bookEvaluator.notifyDeleteCell(new HSSFEvaluationCell((ACell) cell));
    }


    public void notifySetFormula(ICell cell) {
        _bookEvaluator.notifyUpdateCell(new HSSFEvaluationCell((ACell) cell));
    }


    public CellValue evaluate(ICell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_BOOLEAN:
                return CellValue.valueOf(cell.getBooleanCellValue());
            case HSSFCell.CELL_TYPE_ERROR:
                return CellValue.getError(cell.getErrorCellValue());
            case HSSFCell.CELL_TYPE_FORMULA:
                return evaluateFormulaCellValue(cell);
            case HSSFCell.CELL_TYPE_NUMERIC:
                return new CellValue(cell.getNumericCellValue());
            case HSSFCell.CELL_TYPE_STRING:
                return new CellValue(cell.getRichStringCellValue().getString());
            case HSSFCell.CELL_TYPE_BLANK:
                return null;
        }
        throw new IllegalStateException("Bad cell type (" + cell.getCellType() + ")");
    }


    public int evaluateFormulaCell(ICell cell) {
        if (cell == null || cell.getCellType() != HSSFCell.CELL_TYPE_FORMULA) {
            return -1;
        }
        CellValue cv = evaluateFormulaCellValue(cell);

        setCellValue(cell, cv);
        return cv.getCellType();
    }


    public HSSFCell evaluateInCell(ICell cell) {
        if (cell == null) {
            return null;
        }
        HSSFCell result = (HSSFCell) cell;
        if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
            CellValue cv = evaluateFormulaCellValue(cell);
            setCellValue(cell, cv);
            setCellType(cell, cv);
        }
        return result;
    }


    public void evaluateAll() {
        evaluateAllFormulaCells(_book, this);
    }


    public CellValue evaluateFormulaCellValue(ICell cell) {
        if (hssfEvaluationCell != null) {

            hssfEvaluationCell.setHSSFCell((ACell) cell);
        } else {
            hssfEvaluationCell = new HSSFEvaluationCell((ACell) cell);
        }

        _bookEvaluator.clearAllCachedResultValues();

        ValueEval eval = _bookEvaluator.evaluate(hssfEvaluationCell);
        if (eval instanceof NumberEval) {
            NumberEval ne = (NumberEval) eval;
            return new CellValue(ne.getNumberValue());
        }
        if (eval instanceof BoolEval) {
            BoolEval be = (BoolEval) eval;
            return CellValue.valueOf(be.getBooleanValue());
        }
        if (eval instanceof StringEval) {
            StringEval ne = (StringEval) eval;
            return new CellValue(ne.getStringValue());
        }
        if (eval instanceof ErrorEval) {
            return CellValue.getError(((ErrorEval) eval).getErrorCode());
        }
        if (eval == null) {
            return null;
        }
        throw new RuntimeException("Unexpected eval class (" + eval.getClass().getName() + ")");
    }


    public ValueEval evaluateFormulaValueEval(ACell cell) {
        if (hssfEvaluationCell != null) {

            hssfEvaluationCell.setHSSFCell(cell);
        } else {
            hssfEvaluationCell = new HSSFEvaluationCell(cell);
        }

        _bookEvaluator.clearAllCachedResultValues();

        return _bookEvaluator.evaluate(hssfEvaluationCell);
    }


    public ValueEval evaluateFormulaValueEval(HSSFName name) {
        return null;
    }
}
