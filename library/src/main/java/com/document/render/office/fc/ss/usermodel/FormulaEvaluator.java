

package com.document.render.office.fc.ss.usermodel;



public interface FormulaEvaluator {


    void clearAllCachedResultValues();


    void notifySetFormula(ICell cell);


    void notifyDeleteCell(ICell cell);


    void notifyUpdateCell(ICell cell);


    void evaluateAll();


    CellValue evaluate(ICell cell);



    int evaluateFormulaCell(ICell cell);


    ICell evaluateInCell(ICell cell);
}
