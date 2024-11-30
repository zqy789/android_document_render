

package com.document.render.office.fc.hssf.formula;


import com.document.render.office.fc.hssf.formula.eval.AreaEval;
import com.document.render.office.fc.hssf.formula.eval.AreaEvalBase;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.fc.hssf.formula.ptg.AreaI;
import com.document.render.office.fc.hssf.formula.ptg.AreaI.OffsetArea;
import com.document.render.office.fc.ss.util.CellReference;



final class LazyAreaEval extends AreaEvalBase {

    private final SheetRefEvaluator _evaluator;

    LazyAreaEval(AreaI ptg, SheetRefEvaluator evaluator) {
        super(ptg);
        _evaluator = evaluator;
    }

    public LazyAreaEval(int firstRowIndex, int firstColumnIndex, int lastRowIndex,
                        int lastColumnIndex, SheetRefEvaluator evaluator) {
        super(firstRowIndex, firstColumnIndex, lastRowIndex, lastColumnIndex);
        _evaluator = evaluator;
    }

    public ValueEval getRelativeValue(int relativeRowIndex, int relativeColumnIndex) {

        int rowIx = (relativeRowIndex + getFirstRow()) & 0xFFFF;
        int colIx = (relativeColumnIndex + getFirstColumn()) & 0x00FF;

        return _evaluator.getEvalForCell(rowIx, colIx);
    }

    public AreaEval offset(int relFirstRowIx, int relLastRowIx, int relFirstColIx, int relLastColIx) {
        AreaI area = new OffsetArea(getFirstRow(), getFirstColumn(),
                relFirstRowIx, relLastRowIx, relFirstColIx, relLastColIx);

        return new LazyAreaEval(area, _evaluator);
    }

    public LazyAreaEval getRow(int rowIndex) {
        if (rowIndex >= getHeight()) {
            throw new IllegalArgumentException("Invalid rowIndex " + rowIndex
                    + ".  Allowable range is (0.." + getHeight() + ").");
        }
        int absRowIx = getFirstRow() + rowIndex;
        return new LazyAreaEval(absRowIx, getFirstColumn(), absRowIx, getLastColumn(), _evaluator);
    }

    public LazyAreaEval getColumn(int columnIndex) {
        if (columnIndex >= getWidth()) {
            throw new IllegalArgumentException("Invalid columnIndex " + columnIndex
                    + ".  Allowable range is (0.." + getWidth() + ").");
        }
        int absColIx = getFirstColumn() + columnIndex;
        return new LazyAreaEval(getFirstRow(), absColIx, getLastRow(), absColIx, _evaluator);
    }

    public String toString() {
        CellReference crA = new CellReference(getFirstRow(), getFirstColumn());
        CellReference crB = new CellReference(getLastRow(), getLastColumn());
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName()).append("[");
        sb.append(_evaluator.getSheetName());
        sb.append('!');
        sb.append(crA.formatAsString());
        sb.append(':');
        sb.append(crB.formatAsString());
        sb.append("]");
        return sb.toString();
    }


    public boolean isSubTotal(int rowIndex, int columnIndex) {

        return _evaluator.isSubTotal(rowIndex, columnIndex);
    }
}
