

package com.document.render.office.fc.hssf.formula;

import com.document.render.office.fc.hssf.formula.eval.BlankEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



final class EvaluationTracker {

    private final List<CellEvaluationFrame> _evaluationFrames;
    private final Set<FormulaCellCacheEntry> _currentlyEvaluatingCells;
    private final EvaluationCache _cache;

    public EvaluationTracker(EvaluationCache cache) {
        _cache = cache;
        _evaluationFrames = new ArrayList<CellEvaluationFrame>();
        _currentlyEvaluatingCells = new HashSet<FormulaCellCacheEntry>();
    }


    public boolean startEvaluate(FormulaCellCacheEntry cce) {
        if (cce == null) {
            throw new IllegalArgumentException("cellLoc must not be null");
        }
        if (_currentlyEvaluatingCells.contains(cce)) {
            return false;
        }
        _currentlyEvaluatingCells.add(cce);
        _evaluationFrames.add(new CellEvaluationFrame(cce));
        return true;
    }

    public void updateCacheResult(ValueEval result) {

        int nFrames = _evaluationFrames.size();
        if (nFrames < 1) {
            throw new IllegalStateException("Call to endEvaluate without matching call to startEvaluate");
        }
        CellEvaluationFrame frame = _evaluationFrames.get(nFrames - 1);
        if (result == ErrorEval.CIRCULAR_REF_ERROR && nFrames > 1) {






            return;
        }

        frame.updateFormulaResult(result);
    }


    public void endEvaluate(CellCacheEntry cce) {

        int nFrames = _evaluationFrames.size();
        if (nFrames < 1) {
            throw new IllegalStateException("Call to endEvaluate without matching call to startEvaluate");
        }

        nFrames--;
        CellEvaluationFrame frame = _evaluationFrames.get(nFrames);
        if (cce != frame.getCCE()) {
            throw new IllegalStateException("Wrong cell specified. ");
        }

        _evaluationFrames.remove(nFrames);
        _currentlyEvaluatingCells.remove(cce);
    }

    public void acceptFormulaDependency(CellCacheEntry cce) {

        int prevFrameIndex = _evaluationFrames.size() - 1;
        if (prevFrameIndex < 0) {

        } else {
            CellEvaluationFrame consumingFrame = _evaluationFrames.get(prevFrameIndex);
            consumingFrame.addSensitiveInputCell(cce);
        }
    }

    public void acceptPlainValueDependency(int bookIndex, int sheetIndex,
                                           int rowIndex, int columnIndex, ValueEval value) {

        int prevFrameIndex = _evaluationFrames.size() - 1;
        if (prevFrameIndex < 0) {

        } else {
            CellEvaluationFrame consumingFrame = _evaluationFrames.get(prevFrameIndex);
            if (value == BlankEval.instance) {
                consumingFrame.addUsedBlankCell(bookIndex, sheetIndex, rowIndex, columnIndex);
            } else {
                PlainValueCellCacheEntry cce = _cache.getPlainValueEntry(bookIndex, sheetIndex,
                        rowIndex, columnIndex, value);
                consumingFrame.addSensitiveInputCell(cce);
            }
        }
    }
}
