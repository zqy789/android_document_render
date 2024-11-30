

package com.document.render.office.fc.hssf.formula;

import com.document.render.office.fc.hssf.formula.eval.ValueEval;

import java.util.HashSet;
import java.util.Set;



final class CellEvaluationFrame {

    private final FormulaCellCacheEntry _cce;
    private final Set<CellCacheEntry> _sensitiveInputCells;
    private FormulaUsedBlankCellSet _usedBlankCellGroup;

    public CellEvaluationFrame(FormulaCellCacheEntry cce) {
        _cce = cce;
        _sensitiveInputCells = new HashSet<CellCacheEntry>();
    }

    public CellCacheEntry getCCE() {
        return _cce;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName()).append(" [");
        sb.append("]");
        return sb.toString();
    }


    public void addSensitiveInputCell(CellCacheEntry inputCell) {
        _sensitiveInputCells.add(inputCell);
    }


    private CellCacheEntry[] getSensitiveInputCells() {
        int nItems = _sensitiveInputCells.size();
        if (nItems < 1) {
            return CellCacheEntry.EMPTY_ARRAY;
        }
        CellCacheEntry[] result = new CellCacheEntry[nItems];
        _sensitiveInputCells.toArray(result);
        return result;
    }

    public void addUsedBlankCell(int bookIndex, int sheetIndex, int rowIndex, int columnIndex) {
        if (_usedBlankCellGroup == null) {
            _usedBlankCellGroup = new FormulaUsedBlankCellSet();
        }
        _usedBlankCellGroup.addCell(bookIndex, sheetIndex, rowIndex, columnIndex);
    }

    public void updateFormulaResult(ValueEval result) {
        _cce.updateFormulaResult(result, getSensitiveInputCells(), _usedBlankCellGroup);
    }
}
