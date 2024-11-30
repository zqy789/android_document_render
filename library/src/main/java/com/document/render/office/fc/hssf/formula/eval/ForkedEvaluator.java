

package com.document.render.office.fc.hssf.formula.eval;


import androidx.annotation.Keep;

import com.document.render.office.fc.hssf.formula.CollaboratingWorkbooksEnvironment;
import com.document.render.office.fc.hssf.formula.EvaluationCell;
import com.document.render.office.fc.hssf.formula.EvaluationWorkbook;
import com.document.render.office.fc.hssf.formula.IStabilityClassifier;
import com.document.render.office.fc.hssf.formula.WorkbookEvaluator;
import com.document.render.office.fc.hssf.formula.udf.UDFFinder;
import com.document.render.office.fc.hssf.usermodel.HSSFEvaluationWorkbook;
import com.document.render.office.fc.hssf.usermodel.HSSFWorkbook;
import com.document.render.office.fc.ss.usermodel.ICell;
import com.document.render.office.fc.ss.usermodel.Workbook;
import com.document.render.office.ss.model.XLSModel.AWorkbook;



public final class ForkedEvaluator {

    private WorkbookEvaluator _evaluator;
    private ForkedEvaluationWorkbook _sewb;

    private ForkedEvaluator(EvaluationWorkbook masterWorkbook, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
        _sewb = new ForkedEvaluationWorkbook(masterWorkbook);
        _evaluator = new WorkbookEvaluator(_sewb, stabilityClassifier, udfFinder);
    }

    private static EvaluationWorkbook createEvaluationWorkbook(Workbook wb) {
        if (wb instanceof AWorkbook) {
            return HSSFEvaluationWorkbook.create((AWorkbook) wb);
        }




        throw new IllegalArgumentException("Unexpected workbook type (" + wb.getClass().getName() + ")");
    }


    @Keep
    public static ForkedEvaluator create(Workbook wb, IStabilityClassifier stabilityClassifier) {
        return create(wb, stabilityClassifier, null);
    }


    @Keep
    public static ForkedEvaluator create(Workbook wb, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
        return new ForkedEvaluator(createEvaluationWorkbook(wb), stabilityClassifier, udfFinder);
    }


    public static void setupEnvironment(String[] workbookNames, ForkedEvaluator[] evaluators) {
        WorkbookEvaluator[] wbEvals = new WorkbookEvaluator[evaluators.length];
        for (int i = 0; i < wbEvals.length; i++) {
            wbEvals[i] = evaluators[i]._evaluator;
        }
        CollaboratingWorkbooksEnvironment.setup(workbookNames, wbEvals);
    }


    public void updateCell(String sheetName, int rowIndex, int columnIndex, ValueEval value) {

        ForkedEvaluationCell cell = _sewb.getOrCreateUpdatableCell(sheetName, rowIndex, columnIndex);
        cell.setValue(value);
        _evaluator.notifyUpdateCell(cell);
    }


    public void copyUpdatedCells(Workbook workbook) {
        _sewb.copyUpdatedCells(workbook);
    }


    public ValueEval evaluate(String sheetName, int rowIndex, int columnIndex) {
        EvaluationCell cell = _sewb.getEvaluationCell(sheetName, rowIndex, columnIndex);

        switch (cell.getCellType()) {
            case ICell.CELL_TYPE_BOOLEAN:
                return BoolEval.valueOf(cell.getBooleanCellValue());
            case ICell.CELL_TYPE_ERROR:
                return ErrorEval.valueOf(cell.getErrorCellValue());
            case ICell.CELL_TYPE_FORMULA:
                return _evaluator.evaluate(cell);
            case ICell.CELL_TYPE_NUMERIC:
                return new NumberEval(cell.getNumericCellValue());
            case ICell.CELL_TYPE_STRING:
                return new StringEval(cell.getStringCellValue());
            case ICell.CELL_TYPE_BLANK:
                return null;
        }
        throw new IllegalStateException("Bad cell type (" + cell.getCellType() + ")");
    }
}
