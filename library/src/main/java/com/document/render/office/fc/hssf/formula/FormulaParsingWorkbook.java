

package com.document.render.office.fc.hssf.formula;


import com.document.render.office.fc.hssf.formula.ptg.NameXPtg;
import com.document.render.office.fc.ss.SpreadsheetVersion;



public interface FormulaParsingWorkbook {

    EvaluationName getName(String name, int sheetIndex);

    NameXPtg getNameXPtg(String name);


    int getExternalSheetIndex(String sheetName);


    int getExternalSheetIndex(String workbookName, String sheetName);


    SpreadsheetVersion getSpreadsheetVersion();

}
