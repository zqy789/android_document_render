

package com.document.render.office.fc.hssf.formula;

import com.document.render.office.fc.hssf.formula.ptg.Area2DPtgBase;
import com.document.render.office.fc.hssf.formula.ptg.Area3DPtg;
import com.document.render.office.fc.hssf.formula.ptg.AreaErrPtg;
import com.document.render.office.fc.hssf.formula.ptg.AreaPtg;
import com.document.render.office.fc.hssf.formula.ptg.AreaPtgBase;
import com.document.render.office.fc.hssf.formula.ptg.DeletedArea3DPtg;
import com.document.render.office.fc.hssf.formula.ptg.DeletedRef3DPtg;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.formula.ptg.Ref3DPtg;
import com.document.render.office.fc.hssf.formula.ptg.RefErrorPtg;
import com.document.render.office.fc.hssf.formula.ptg.RefPtg;
import com.document.render.office.fc.hssf.formula.ptg.RefPtgBase;



public final class FormulaShifter {


    private final int _externSheetIndex;
    private final int _firstMovedIndex;
    private final int _lastMovedIndex;
    private final int _amountToMove;
    private final int _srcSheetIndex;
    private final int _dstSheetIndex;
    private final ShiftMode _mode;


    private FormulaShifter(int externSheetIndex, int firstMovedIndex, int lastMovedIndex, int amountToMove) {
        if (amountToMove == 0) {
            throw new IllegalArgumentException("amountToMove must not be zero");
        }
        if (firstMovedIndex > lastMovedIndex) {
            throw new IllegalArgumentException("firstMovedIndex, lastMovedIndex out of order");
        }
        _externSheetIndex = externSheetIndex;
        _firstMovedIndex = firstMovedIndex;
        _lastMovedIndex = lastMovedIndex;
        _amountToMove = amountToMove;
        _mode = ShiftMode.Row;

        _srcSheetIndex = _dstSheetIndex = -1;
    }


    private FormulaShifter(int srcSheetIndex, int dstSheetIndex) {
        _externSheetIndex = _firstMovedIndex = _lastMovedIndex = _amountToMove = -1;

        _srcSheetIndex = srcSheetIndex;
        _dstSheetIndex = dstSheetIndex;
        _mode = ShiftMode.Sheet;
    }

    public static FormulaShifter createForRowShift(int externSheetIndex, int firstMovedRowIndex, int lastMovedRowIndex, int numberOfRowsToMove) {
        return new FormulaShifter(externSheetIndex, firstMovedRowIndex, lastMovedRowIndex, numberOfRowsToMove);
    }

    public static FormulaShifter createForSheetShift(int srcSheetIndex, int dstSheetIndex) {
        return new FormulaShifter(srcSheetIndex, dstSheetIndex);
    }

    private static Ptg createDeletedRef(Ptg ptg) {
        if (ptg instanceof RefPtg) {
            return new RefErrorPtg();
        }
        if (ptg instanceof Ref3DPtg) {
            Ref3DPtg rptg = (Ref3DPtg) ptg;
            return new DeletedRef3DPtg(rptg.getExternSheetIndex());
        }
        if (ptg instanceof AreaPtg) {
            return new AreaErrPtg();
        }
        if (ptg instanceof Area3DPtg) {
            Area3DPtg area3DPtg = (Area3DPtg) ptg;
            return new DeletedArea3DPtg(area3DPtg.getExternSheetIndex());
        }

        throw new IllegalArgumentException("Unexpected ref ptg class (" + ptg.getClass().getName() + ")");
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(getClass().getName());
        sb.append(" [");
        sb.append(_firstMovedIndex);
        sb.append(_lastMovedIndex);
        sb.append(_amountToMove);
        return sb.toString();
    }


    public boolean adjustFormula(Ptg[] ptgs, int currentExternSheetIx) {
        boolean refsWereChanged = false;
        for (int i = 0; i < ptgs.length; i++) {
            Ptg newPtg = adjustPtg(ptgs[i], currentExternSheetIx);
            if (newPtg != null) {
                refsWereChanged = true;
                ptgs[i] = newPtg;
            }
        }
        return refsWereChanged;
    }

    private Ptg adjustPtg(Ptg ptg, int currentExternSheetIx) {
        switch (_mode) {
            case Row:
                return adjustPtgDueToRowMove(ptg, currentExternSheetIx);
            case Sheet:
                return adjustPtgDueToShiftMove(ptg);
            default:
                throw new IllegalStateException("Unsupported shift mode: " + _mode);
        }
    }


    private Ptg adjustPtgDueToRowMove(Ptg ptg, int currentExternSheetIx) {
        if (ptg instanceof RefPtg) {
            if (currentExternSheetIx != _externSheetIndex) {

                return null;
            }
            RefPtg rptg = (RefPtg) ptg;
            return rowMoveRefPtg(rptg);
        }
        if (ptg instanceof Ref3DPtg) {
            Ref3DPtg rptg = (Ref3DPtg) ptg;
            if (_externSheetIndex != rptg.getExternSheetIndex()) {


                return null;
            }
            return rowMoveRefPtg(rptg);
        }
        if (ptg instanceof Area2DPtgBase) {
            if (currentExternSheetIx != _externSheetIndex) {

                return ptg;
            }
            return rowMoveAreaPtg((Area2DPtgBase) ptg);
        }
        if (ptg instanceof Area3DPtg) {
            Area3DPtg aptg = (Area3DPtg) ptg;
            if (_externSheetIndex != aptg.getExternSheetIndex()) {


                return null;
            }
            return rowMoveAreaPtg(aptg);
        }
        return null;
    }

    private Ptg adjustPtgDueToShiftMove(Ptg ptg) {
        Ptg updatedPtg = null;
        if (ptg instanceof Ref3DPtg) {
            Ref3DPtg ref = (Ref3DPtg) ptg;
            if (ref.getExternSheetIndex() == _srcSheetIndex) {
                ref.setExternSheetIndex(_dstSheetIndex);
                updatedPtg = ref;
            } else if (ref.getExternSheetIndex() == _dstSheetIndex) {
                ref.setExternSheetIndex(_srcSheetIndex);
                updatedPtg = ref;
            }
        }
        return updatedPtg;
    }

    private Ptg rowMoveRefPtg(RefPtgBase rptg) {
        int refRow = rptg.getRow();
        if (_firstMovedIndex <= refRow && refRow <= _lastMovedIndex) {


            rptg.setRow(refRow + _amountToMove);
            return rptg;
        }


        int destFirstRowIndex = _firstMovedIndex + _amountToMove;
        int destLastRowIndex = _lastMovedIndex + _amountToMove;




        if (destLastRowIndex < refRow || refRow < destFirstRowIndex) {

            return null;
        }

        if (destFirstRowIndex <= refRow && refRow <= destLastRowIndex) {

            return createDeletedRef(rptg);
        }
        throw new IllegalStateException("Situation not covered: (" + _firstMovedIndex + ", " +
                _lastMovedIndex + ", " + _amountToMove + ", " + refRow + ", " + refRow + ")");
    }

    private Ptg rowMoveAreaPtg(AreaPtgBase aptg) {
        int aFirstRow = aptg.getFirstRow();
        int aLastRow = aptg.getLastRow();
        if (_firstMovedIndex <= aFirstRow && aLastRow <= _lastMovedIndex) {


            aptg.setFirstRow(aFirstRow + _amountToMove);
            aptg.setLastRow(aLastRow + _amountToMove);
            return aptg;
        }


        int destFirstRowIndex = _firstMovedIndex + _amountToMove;
        int destLastRowIndex = _lastMovedIndex + _amountToMove;

        if (aFirstRow < _firstMovedIndex && _lastMovedIndex < aLastRow) {




            if (destFirstRowIndex < aFirstRow && aFirstRow <= destLastRowIndex) {

                aptg.setFirstRow(destLastRowIndex + 1);
                return aptg;
            } else if (destFirstRowIndex <= aLastRow && aLastRow < destLastRowIndex) {

                aptg.setLastRow(destFirstRowIndex - 1);
                return aptg;
            }


            return null;
        }
        if (_firstMovedIndex <= aFirstRow && aFirstRow <= _lastMovedIndex) {


            if (_amountToMove < 0) {

                aptg.setFirstRow(aFirstRow + _amountToMove);
                return aptg;
            }
            if (destFirstRowIndex > aLastRow) {

                return null;
            }
            int newFirstRowIx = aFirstRow + _amountToMove;
            if (destLastRowIndex < aLastRow) {


                aptg.setFirstRow(newFirstRowIx);
                return aptg;
            }

            int areaRemainingTopRowIx = _lastMovedIndex + 1;
            if (destFirstRowIndex > areaRemainingTopRowIx) {

                newFirstRowIx = areaRemainingTopRowIx;
            }
            aptg.setFirstRow(newFirstRowIx);
            aptg.setLastRow(Math.max(aLastRow, destLastRowIndex));
            return aptg;
        }
        if (_firstMovedIndex <= aLastRow && aLastRow <= _lastMovedIndex) {


            if (_amountToMove > 0) {

                aptg.setLastRow(aLastRow + _amountToMove);
                return aptg;
            }
            if (destLastRowIndex < aFirstRow) {

                return null;
            }
            int newLastRowIx = aLastRow + _amountToMove;
            if (destFirstRowIndex > aFirstRow) {


                aptg.setLastRow(newLastRowIx);
                return aptg;
            }

            int areaRemainingBottomRowIx = _firstMovedIndex - 1;
            if (destLastRowIndex < areaRemainingBottomRowIx) {

                newLastRowIx = areaRemainingBottomRowIx;
            }
            aptg.setFirstRow(Math.min(aFirstRow, destFirstRowIndex));
            aptg.setLastRow(newLastRowIx);
            return aptg;
        }



        if (destLastRowIndex < aFirstRow || aLastRow < destFirstRowIndex) {

            return null;
        }

        if (destFirstRowIndex <= aFirstRow && aLastRow <= destLastRowIndex) {

            return createDeletedRef(aptg);
        }

        if (aFirstRow <= destFirstRowIndex && destLastRowIndex <= aLastRow) {

            return null;
        }

        if (destFirstRowIndex < aFirstRow && aFirstRow <= destLastRowIndex) {


            aptg.setFirstRow(destLastRowIndex + 1);
            return aptg;
        }
        if (destFirstRowIndex < aLastRow && aLastRow <= destLastRowIndex) {


            aptg.setLastRow(destFirstRowIndex - 1);
            return aptg;
        }
        throw new IllegalStateException("Situation not covered: (" + _firstMovedIndex + ", " +
                _lastMovedIndex + ", " + _amountToMove + ", " + aFirstRow + ", " + aLastRow + ")");
    }

    static enum ShiftMode {
        Row,
        Sheet
    }
}
