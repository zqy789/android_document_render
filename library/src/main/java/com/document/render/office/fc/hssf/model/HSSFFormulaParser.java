

package com.document.render.office.fc.hssf.model;


import com.document.render.office.fc.hssf.formula.FormulaParseException;
import com.document.render.office.fc.hssf.formula.FormulaParser;
import com.document.render.office.fc.hssf.formula.FormulaParsingWorkbook;
import com.document.render.office.fc.hssf.formula.FormulaRenderer;
import com.document.render.office.fc.hssf.formula.FormulaType;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.usermodel.HSSFEvaluationWorkbook;
import com.document.render.office.fc.hssf.usermodel.HSSFWorkbook;
import com.document.render.office.ss.model.XLSModel.AWorkbook;



public final class HSSFFormulaParser {

    private HSSFFormulaParser() {
        
    }

    private static FormulaParsingWorkbook createParsingWorkbook(AWorkbook book) {
        return HSSFEvaluationWorkbook.create(book);
    }

    
    public static Ptg[] parse(String formula, AWorkbook workbook) throws FormulaParseException {
        return parse(formula, workbook, FormulaType.CELL);
    }

    
    public static Ptg[] parse(String formula, AWorkbook workbook, int formulaType) throws FormulaParseException {
        return parse(formula, workbook, formulaType, -1);
    }

    
    public static Ptg[] parse(String formula, AWorkbook workbook, int formulaType, int sheetIndex) throws FormulaParseException {
        return FormulaParser.parse(formula, createParsingWorkbook(workbook), formulaType, sheetIndex);
    }

    
    public static String toFormulaString(AWorkbook book, Ptg[] ptgs) {
        return FormulaRenderer.toFormulaString(HSSFEvaluationWorkbook.create(book), ptgs);
    }
}
