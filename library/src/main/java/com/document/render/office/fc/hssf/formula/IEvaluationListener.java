

package com.document.render.office.fc.hssf.formula;

import com.document.render.office.fc.hssf.formula.eval.ValueEval;


interface IEvaluationListener {
    void onCacheHit(int sheetIndex, int rowIndex, int columnIndex, ValueEval result);

    void onReadPlainValue(int sheetIndex, int rowIndex, int columnIndex, ICacheEntry entry);

    void onStartEvaluate(EvaluationCell cell, ICacheEntry entry);

    void onEndEvaluate(ICacheEntry entry, ValueEval result);

    void onClearWholeCache();

    void onClearCachedValue(ICacheEntry entry);


    void sortDependentCachedValues(ICacheEntry[] formulaCells);

    void onClearDependentCachedValue(ICacheEntry formulaCell, int depth);

    void onChangeFromBlankValue(int sheetIndex, int rowIndex, int columnIndex,
                                EvaluationCell cell, ICacheEntry entry);


    interface ICacheEntry {
        ValueEval getValue();
    }
}
