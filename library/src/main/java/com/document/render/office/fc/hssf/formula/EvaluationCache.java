

package com.document.render.office.fc.hssf.formula;


import com.document.render.office.fc.hssf.formula.FormulaCellCache.IEntryOperation;
import com.document.render.office.fc.hssf.formula.FormulaUsedBlankCellSet.BookSheetKey;
import com.document.render.office.fc.hssf.formula.PlainCellCache.Loc;
import com.document.render.office.fc.hssf.formula.eval.BlankEval;
import com.document.render.office.fc.hssf.formula.eval.BoolEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.StringEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.fc.ss.usermodel.ICell;



final class EvaluationCache {


    final IEvaluationListener _evaluationListener;
    private final PlainCellCache _plainCellCache;
    private final FormulaCellCache _formulaCellCache;

    EvaluationCache(IEvaluationListener evaluationListener) {
        _evaluationListener = evaluationListener;
        _plainCellCache = new PlainCellCache();
        _formulaCellCache = new FormulaCellCache();
    }

    public void notifyUpdateCell(int bookIndex, int sheetIndex, EvaluationCell cell) {
        FormulaCellCacheEntry fcce = _formulaCellCache.get(cell);

        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();
        Loc loc = new Loc(bookIndex, sheetIndex, rowIndex, columnIndex);
        PlainValueCellCacheEntry pcce = _plainCellCache.get(loc);

        if (cell.getCellType() == ICell.CELL_TYPE_FORMULA) {
            if (fcce == null) {
                fcce = new FormulaCellCacheEntry();
                if (pcce == null) {
                    if (_evaluationListener != null) {
                        _evaluationListener.onChangeFromBlankValue(sheetIndex, rowIndex,
                                columnIndex, cell, fcce);
                    }
                    updateAnyBlankReferencingFormulas(bookIndex, sheetIndex, rowIndex,
                            columnIndex);
                }
                _formulaCellCache.put(cell, fcce);
            } else {
                fcce.recurseClearCachedFormulaResults(_evaluationListener);
                fcce.clearFormulaEntry();
            }
            if (pcce == null) {

            } else {

                pcce.recurseClearCachedFormulaResults(_evaluationListener);
                _plainCellCache.remove(loc);
            }
        } else {
            ValueEval value = WorkbookEvaluator.getValueFromNonFormulaCell(cell);
            if (pcce == null) {
                if (value != BlankEval.instance) {



                    pcce = new PlainValueCellCacheEntry(value);
                    if (fcce == null) {
                        if (_evaluationListener != null) {
                            _evaluationListener.onChangeFromBlankValue(sheetIndex, rowIndex, columnIndex, cell, pcce);
                        }
                        updateAnyBlankReferencingFormulas(bookIndex, sheetIndex,
                                rowIndex, columnIndex);
                    }
                    _plainCellCache.put(loc, pcce);
                }
            } else {
                if (pcce.updateValue(value)) {
                    pcce.recurseClearCachedFormulaResults(_evaluationListener);
                }
                if (value == BlankEval.instance) {
                    _plainCellCache.remove(loc);
                }
            }
            if (fcce == null) {

            } else {

                _formulaCellCache.remove(cell);
                fcce.setSensitiveInputCells(null);
                fcce.recurseClearCachedFormulaResults(_evaluationListener);
            }
        }
    }

    private void updateAnyBlankReferencingFormulas(int bookIndex, int sheetIndex,
                                                   final int rowIndex, final int columnIndex) {
        final BookSheetKey bsk = new BookSheetKey(bookIndex, sheetIndex);
        _formulaCellCache.applyOperation(new IEntryOperation() {

            public void processEntry(FormulaCellCacheEntry entry) {
                entry.notifyUpdatedBlankCell(bsk, rowIndex, columnIndex, _evaluationListener);
            }
        });
    }

    public PlainValueCellCacheEntry getPlainValueEntry(int bookIndex, int sheetIndex,
                                                       int rowIndex, int columnIndex, ValueEval value) {

        Loc loc = new Loc(bookIndex, sheetIndex, rowIndex, columnIndex);
        PlainValueCellCacheEntry result = _plainCellCache.get(loc);
        if (result == null) {
            result = new PlainValueCellCacheEntry(value);
            _plainCellCache.put(loc, result);
            if (_evaluationListener != null) {
                _evaluationListener.onReadPlainValue(sheetIndex, rowIndex, columnIndex, result);
            }
        } else {

            if (!areValuesEqual(result.getValue(), value)) {
                throw new IllegalStateException("value changed");
            }
            if (_evaluationListener != null) {
                _evaluationListener.onCacheHit(sheetIndex, rowIndex, columnIndex, value);
            }
        }
        return result;
    }

    private boolean areValuesEqual(ValueEval a, ValueEval b) {
        if (a == null) {
            return false;
        }
        Class<?> cls = a.getClass();
        if (cls != b.getClass()) {

            return false;
        }
        if (a == BlankEval.instance) {
            return b == a;
        }
        if (cls == NumberEval.class) {
            return ((NumberEval) a).getNumberValue() == ((NumberEval) b).getNumberValue();
        }
        if (cls == StringEval.class) {
            return ((StringEval) a).getStringValue().equals(((StringEval) b).getStringValue());
        }
        if (cls == BoolEval.class) {
            return ((BoolEval) a).getBooleanValue() == ((BoolEval) b).getBooleanValue();
        }
        if (cls == ErrorEval.class) {
            return ((ErrorEval) a).getErrorCode() == ((ErrorEval) b).getErrorCode();
        }
        throw new IllegalStateException("Unexpected value class (" + cls.getName() + ")");
    }

    public FormulaCellCacheEntry getOrCreateFormulaCellEntry(EvaluationCell cell) {
        FormulaCellCacheEntry result = _formulaCellCache.get(cell);
        if (result == null) {

            result = new FormulaCellCacheEntry();
            _formulaCellCache.put(cell, result);
        }
        return result;
    }


    public void clear() {
        if (_evaluationListener != null) {
            _evaluationListener.onClearWholeCache();
        }
        _plainCellCache.clear();
        _formulaCellCache.clear();
    }

    public void notifyDeleteCell(int bookIndex, int sheetIndex, EvaluationCell cell) {

        if (cell.getCellType() == ICell.CELL_TYPE_FORMULA) {
            FormulaCellCacheEntry fcce = _formulaCellCache.remove(cell);
            if (fcce == null) {

            } else {
                fcce.setSensitiveInputCells(null);
                fcce.recurseClearCachedFormulaResults(_evaluationListener);
            }
        } else {
            Loc loc = new Loc(bookIndex, sheetIndex, cell.getRowIndex(), cell.getColumnIndex());
            PlainValueCellCacheEntry pcce = _plainCellCache.get(loc);

            if (pcce == null) {

            } else {
                pcce.recurseClearCachedFormulaResults(_evaluationListener);
            }
        }
    }
}
