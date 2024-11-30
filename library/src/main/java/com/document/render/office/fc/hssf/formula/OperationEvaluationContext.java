

package com.document.render.office.fc.hssf.formula;

import com.document.render.office.fc.hssf.formula.CollaboratingWorkbooksEnvironment.WorkbookNotFoundException;
import com.document.render.office.fc.hssf.formula.EvaluationWorkbook.ExternalName;
import com.document.render.office.fc.hssf.formula.EvaluationWorkbook.ExternalSheet;
import com.document.render.office.fc.hssf.formula.eval.AreaEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.NameXEval;
import com.document.render.office.fc.hssf.formula.eval.RefEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.fc.hssf.formula.function.FreeRefFunction;
import com.document.render.office.fc.hssf.formula.ptg.Area3DPtg;
import com.document.render.office.fc.hssf.formula.ptg.NameXPtg;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.formula.ptg.Ref3DPtg;
import com.document.render.office.fc.ss.SpreadsheetVersion;
import com.document.render.office.fc.ss.util.CellReference;
import com.document.render.office.fc.ss.util.CellReference.NameType;



public final class OperationEvaluationContext {
    public static final FreeRefFunction UDF = UserDefinedFunction.instance;
    private final EvaluationWorkbook _workbook;
    private final int _sheetIndex;
    private final int _rowIndex;
    private final int _columnIndex;
    private final EvaluationTracker _tracker;
    private final WorkbookEvaluator _bookEvaluator;

    private SheetRefEvaluator _sre;

    public OperationEvaluationContext(WorkbookEvaluator bookEvaluator, EvaluationWorkbook workbook, int sheetIndex, int srcRowNum,
                                      int srcColNum, EvaluationTracker tracker) {
        _bookEvaluator = bookEvaluator;
        _workbook = workbook;
        _sheetIndex = sheetIndex;
        _rowIndex = srcRowNum;
        _columnIndex = srcColNum;
        _tracker = tracker;

        _sre = new SheetRefEvaluator(_bookEvaluator, _tracker, _sheetIndex);
    }

    private static int parseRowRef(String refStrPart) {
        return CellReference.convertColStringToIndex(refStrPart);
    }

    private static int parseColRef(String refStrPart) {
        return Integer.parseInt(refStrPart) - 1;
    }

    private static NameType classifyCellReference(String str, SpreadsheetVersion ssVersion) {
        int len = str.length();
        if (len < 1) {
            return CellReference.NameType.BAD_CELL_OR_NAMED_RANGE;
        }
        return CellReference.classifyCellReference(str, ssVersion);
    }

    public EvaluationWorkbook getWorkbook() {
        return _workbook;
    }

    public int getRowIndex() {
        return _rowIndex;
    }

    public int getColumnIndex() {
        return _columnIndex;
    }

    SheetRefEvaluator createExternSheetRefEvaluator(ExternSheetReferenceToken ptg) {
        return createExternSheetRefEvaluator(ptg.getExternSheetIndex());
    }

    SheetRefEvaluator createExternSheetRefEvaluator(int externSheetIndex) {
        ExternalSheet externalSheet = _workbook.getExternalSheet(externSheetIndex);
        WorkbookEvaluator targetEvaluator;
        int otherSheetIndex;
        if (externalSheet == null) {

            otherSheetIndex = _workbook.convertFromExternSheetIndex(externSheetIndex);
            targetEvaluator = _bookEvaluator;
        } else {

            String workbookName = externalSheet.getWorkbookName();
            try {
                targetEvaluator = _bookEvaluator.getOtherWorkbookEvaluator(workbookName);
            } catch (WorkbookNotFoundException e) {
                throw new RuntimeException(e.getMessage());
            }
            otherSheetIndex = targetEvaluator.getSheetIndex(externalSheet.getSheetName());
            if (otherSheetIndex < 0) {
                throw new RuntimeException("Invalid sheet name '" + externalSheet.getSheetName()
                        + "' in bool '" + workbookName + "'.");
            }
        }
        return new SheetRefEvaluator(targetEvaluator, _tracker, otherSheetIndex);
    }


    private SheetRefEvaluator createExternSheetRefEvaluator(String workbookName, String sheetName) {
        WorkbookEvaluator targetEvaluator;
        if (workbookName == null) {
            targetEvaluator = _bookEvaluator;
        } else {
            if (sheetName == null) {
                throw new IllegalArgumentException("sheetName must not be null if workbookName is provided");
            }
            try {
                targetEvaluator = _bookEvaluator.getOtherWorkbookEvaluator(workbookName);
            } catch (WorkbookNotFoundException e) {
                return null;
            }
        }
        int otherSheetIndex = sheetName == null ? _sheetIndex : targetEvaluator.getSheetIndex(sheetName);
        if (otherSheetIndex < 0) {
            return null;
        }
        return new SheetRefEvaluator(targetEvaluator, _tracker, otherSheetIndex);
    }

    public SheetRefEvaluator getRefEvaluatorForCurrentSheet() {
        if (_sre == null) {
            _sre = new SheetRefEvaluator(_bookEvaluator, _tracker, _sheetIndex);
        }
        return _sre;
    }


    public ValueEval getDynamicReference(String workbookName, String sheetName, String refStrPart1,
                                         String refStrPart2, boolean isA1Style) {
        if (!isA1Style) {
            throw new RuntimeException("R1C1 style not supported yet");
        }
        SheetRefEvaluator sre = createExternSheetRefEvaluator(workbookName, sheetName);
        if (sre == null) {
            return ErrorEval.REF_INVALID;
        }

        SpreadsheetVersion ssVersion = ((FormulaParsingWorkbook) _workbook).getSpreadsheetVersion();

        NameType part1refType = classifyCellReference(refStrPart1, ssVersion);
        switch (part1refType) {
            case BAD_CELL_OR_NAMED_RANGE:
                return ErrorEval.REF_INVALID;
            case NAMED_RANGE:
                EvaluationName nm = ((FormulaParsingWorkbook) _workbook).getName(refStrPart1, _sheetIndex);
                if (!nm.isRange()) {
                    throw new RuntimeException("Specified name '" + refStrPart1 + "' is not a range as expected.");
                }
                return _bookEvaluator.evaluateNameFormula(nm.getNameDefinition(), this);
        }
        if (refStrPart2 == null) {

            switch (part1refType) {
                case COLUMN:
                case ROW:
                    return ErrorEval.REF_INVALID;
                case CELL:
                    CellReference cr = new CellReference(refStrPart1);
                    return new LazyRefEval(cr.getRow(), cr.getCol(), sre);
            }
            throw new IllegalStateException("Unexpected reference classification of '" + refStrPart1 + "'.");
        }
        NameType part2refType = classifyCellReference(refStrPart1, ssVersion);
        switch (part2refType) {
            case BAD_CELL_OR_NAMED_RANGE:
                return ErrorEval.REF_INVALID;
            case NAMED_RANGE:
                throw new RuntimeException("Cannot evaluate '" + refStrPart1
                        + "'. Indirect evaluation of defined names not supported yet");
        }

        if (part2refType != part1refType) {

            return ErrorEval.REF_INVALID;
        }
        int firstRow, firstCol, lastRow, lastCol;
        switch (part1refType) {
            case COLUMN:
                firstRow = 0;
                lastRow = ssVersion.getLastRowIndex();
                firstCol = parseColRef(refStrPart1);
                lastCol = parseColRef(refStrPart2);
                break;
            case ROW:
                firstCol = 0;
                lastCol = ssVersion.getLastColumnIndex();
                firstRow = parseRowRef(refStrPart1);
                lastRow = parseRowRef(refStrPart2);
                break;
            case CELL:
                CellReference cr;
                cr = new CellReference(refStrPart1);
                firstRow = cr.getRow();
                firstCol = cr.getCol();
                cr = new CellReference(refStrPart2);
                lastRow = cr.getRow();
                lastCol = cr.getCol();
                break;
            default:
                throw new IllegalStateException("Unexpected reference classification of '" + refStrPart1 + "'.");
        }
        return new LazyAreaEval(firstRow, firstCol, lastRow, lastCol, sre);
    }

    public FreeRefFunction findUserDefinedFunction(String functionName) {
        return _bookEvaluator.findUserDefinedFunction(functionName);
    }

    public ValueEval getRefEval(int rowIndex, int columnIndex) {
        SheetRefEvaluator sre = getRefEvaluatorForCurrentSheet();
        return new LazyRefEval(rowIndex, columnIndex, sre);
    }

    public ValueEval getRef3DEval(int rowIndex, int columnIndex, int extSheetIndex) {
        SheetRefEvaluator sre = createExternSheetRefEvaluator(extSheetIndex);
        return new LazyRefEval(rowIndex, columnIndex, sre);
    }

    public ValueEval getAreaEval(int firstRowIndex, int firstColumnIndex,
                                 int lastRowIndex, int lastColumnIndex) {
        SheetRefEvaluator sre = getRefEvaluatorForCurrentSheet();
        return new LazyAreaEval(firstRowIndex, firstColumnIndex, lastRowIndex, lastColumnIndex, sre);
    }

    public ValueEval getArea3DEval(int firstRowIndex, int firstColumnIndex,
                                   int lastRowIndex, int lastColumnIndex, int extSheetIndex) {
        SheetRefEvaluator sre = createExternSheetRefEvaluator(extSheetIndex);
        return new LazyAreaEval(firstRowIndex, firstColumnIndex, lastRowIndex, lastColumnIndex, sre);
    }

    public ValueEval getNameXEval(NameXPtg nameXPtg) {
        ExternalSheet externSheet = _workbook.getExternalSheet(nameXPtg.getSheetRefIndex());
        if (externSheet == null)
            return new NameXEval(nameXPtg);
        String workbookName = externSheet.getWorkbookName();
        ExternalName externName = _workbook.getExternalName(
                nameXPtg.getSheetRefIndex(),
                nameXPtg.getNameIndex()
        );
        try {
            WorkbookEvaluator refWorkbookEvaluator = _bookEvaluator.getOtherWorkbookEvaluator(workbookName);
            EvaluationName evaluationName = refWorkbookEvaluator.getName(externName.getName(), externName.getIx() - 1);
            if (evaluationName != null && evaluationName.hasFormula()) {
                if (evaluationName.getNameDefinition().length > 1) {
                    throw new RuntimeException("Complex name formulas not supported yet");
                }
                Ptg ptg = evaluationName.getNameDefinition()[0];
                if (ptg instanceof Ref3DPtg) {
                    Ref3DPtg ref3D = (Ref3DPtg) ptg;
                    int sheetIndex = refWorkbookEvaluator.getSheetIndexByExternIndex(ref3D.getExternSheetIndex());
                    String sheetName = refWorkbookEvaluator.getSheetName(sheetIndex);
                    SheetRefEvaluator sre = createExternSheetRefEvaluator(workbookName, sheetName);
                    return new LazyRefEval(ref3D.getRow(), ref3D.getColumn(), sre);
                } else if (ptg instanceof Area3DPtg) {
                    Area3DPtg area3D = (Area3DPtg) ptg;
                    int sheetIndex = refWorkbookEvaluator.getSheetIndexByExternIndex(area3D.getExternSheetIndex());
                    String sheetName = refWorkbookEvaluator.getSheetName(sheetIndex);
                    SheetRefEvaluator sre = createExternSheetRefEvaluator(workbookName, sheetName);
                    return new LazyAreaEval(area3D.getFirstRow(), area3D.getFirstColumn(), area3D.getLastRow(), area3D.getLastColumn(), sre);
                }
            }
            return ErrorEval.REF_INVALID;
        } catch (WorkbookNotFoundException wnfe) {
            return ErrorEval.REF_INVALID;
        }
    }
}
